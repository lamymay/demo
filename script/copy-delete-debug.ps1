# 导入必要的命名空间
Add-Type -AssemblyName Microsoft.VisualBasic

# 导入CSV文件
$csvFilePath = "C:\Path\To\servers.csv"  # 替换为你的CSV文件路径
$serversData = Import-Csv -Path $csvFilePath

# 定义变量
$sourcePath = "C:\Path\To\Source\Files"  # 替换为实际路径
$nasPath = "\\NAS\Backup"                # 替换为实际路径
$deleteToRecycleBin = $true  # 控制是否删除到回收站

# 提示输入远程服务器的管理员凭据，只提示一次
$credential = Get-Credential

# 定义要远程执行的脚本块
$scriptBlock = {
    param($sourcePath, $nasPath, $deleteToRecycleBin)

    # 获取当前服务器的主机名
    $hostName = $env:COMPUTERNAME
    # 组合备份路径
    $backupPath = Join-Path -Path $nasPath -ChildPath $hostName

    # 打印开始备份的日志
    Write-Output "[$hostName] 开始备份..."

    try {
        # 检查并创建备份目录
        if (-Not (Test-Path -Path $backupPath)) {
            New-Item -Path $backupPath -ItemType Directory
            Write-Output "[$hostName] 备份目录已创建: $backupPath"
        } else {
            Write-Output "[$hostName] 备份目录已存在: $backupPath"
        }

        # 复制文件到备份目录
        Get-ChildItem -Path $sourcePath | ForEach-Object {
            $destinationFile = Join-Path -Path $backupPath -ChildPath $_.Name
            Copy-Item -Path $_.FullName -Destination $destinationFile
            Write-Output "[$hostName] 备份文件: $_.FullName 到 $destinationFile"
        }

        # 删除源文件的部分被注释掉
        # Get-ChildItem -Path $sourcePath | ForEach-Object {
        #     if ($deleteToRecycleBin) {
        #         [Microsoft.VisualBasic.FileIO.FileSystem]::DeleteFile($_.FullName, 'OnlyErrorDialogs', 'SendToRecycleBin')
        #         Write-Output "[$hostName] 文件已移动到回收站: $_.FullName"
        #     } else {
        #         Remove-Item -Path $_.FullName -Force
        #         Write-Output "[$hostName] 文件已物理删除: $_.FullName"
        #     }
        # }

        # 返回成功消息
        return "成功"
    }
    catch {
        # 返回错误消息
        return "失败: $_"
    }
}

# 用于发送确认键的函数
function Send-Confirmation {
    $signature = @"
    [DllImport("user32.dll", CharSet = CharSet.Auto, SetLastError = true)]
    public static extern void keybd_event(byte bVk, byte bScan, uint dwFlags, int dwExtraInfo);
"@
    Add-Type -MemberDefinition $signature -Name "Win32Keyboard" -Namespace Win32Functions

    # Pressing the Enter key
    [Win32Functions.Win32Keyboard]::keybd_event(0x0D, 0, 0, 0) # Enter down
    [Win32Functions.Win32Keyboard]::keybd_event(0x0D, 0, 2, 0) # Enter up
}

# 遍历所有服务器并执行脚本块
foreach ($serverData in $serversData) {
    $server = $serverData.ServerName  # 获取服务器名称

    # 启动远程桌面连接
    mstsc /v:$server
    Start-Sleep -Seconds 5 # 等待远程桌面连接完成

    # 发送确认键以跳过提示界面
    Send-Confirmation

    # 启动后台作业，使用 -AsJob 参数并行执行
    $job = Invoke-Command -ComputerName $server -ScriptBlock $scriptBlock -ArgumentList $sourcePath, $nasPath, $deleteToRecycleBin -Credential $credential -AsJob

    # 等待作业完成
    while ($job.State -eq 'Running') {
        Start-Sleep -Seconds 1
    }

    # 获取作业结果
    $result = Receive-Job -Job $job
    Remove-Job -Job $job

    # 记录结果到第三列
    $serverData.Result = $result
    Write-Output "[$server] 结果: $result"
}

# 将结果写回CSV文件，保留其他列信息
$serversData | Export-Csv -Path $csvFilePath -NoTypeInformation

Write-Output "所有备份作业已完成."

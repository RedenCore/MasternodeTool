# MasternodeTool by Tri Nguyen

This tool is to help keep masternode node wallet running on linux if it every go down or out of sync.

Requirement:
------------
install lastest java @ https://java.com/en/download/

Setup and Configuration:
------------------------
1. Download lastest release @ https://github.com/RedenCore/MasternodeTool/release
2. Copy and rename reden.properties to whatever coin u like i.e reef.propertoes, mano.properties, etc
3. edit the newly create property file and change the following value
  coinName=coin name and label i.e reden-MN1
  host=ip of vps that host ur masternode
  port=22, ssh port number, usually 22
  username=vps login username
  password=vps login password
  coinCommandPrefix=execution part to coin commands for coind, and coin-cli without d and cli part.
                    for example reden-1.0.2-ubuntu16/reden/reden
  explorerAPIUrl=web address to coin explorer api. for example thttp://explorer.reden.io/api/
4. repeat step 2 and 3
5. edit coin-config-list.txt to include all the properties file created above. one file per line.
6. double click on run.bat

Note: run.bat default to check all the files setup above one every 10 mins. if u want to customize the time it execute in between
then run then open command prompt and nativate to the same folder where u have run.bat and type:
run.bat numberOfMin. 
for example: run.bat 1 to cycle every min

Donation: if you found this useful, please consider donate it in one or more to the below addresses
------------------
btc:18ThpppgPYtcs7J46B8dywxWrHFcpwaLwS
eth:0x4c77b25273632Dc38e0514D6aFCAb95b9F57DA42
ltc:LPH3MECyLsoZEBQppHKphzSARovyU3kUJT
rvn:REiKBxcTDfbZnkDxXxVR3vzW8FZEjS2waS
reden:RNGobMGPS7DHmUZfqotG2RtLJimLAsjMJV

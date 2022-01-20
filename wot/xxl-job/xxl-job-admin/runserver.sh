#!/bin/bash
SOURCE="$0"
while [ -h "$SOURCE"  ]; do # resolve $SOURCE until the file is no longer a symlink
    DIR="$( cd -P "$( dirname "$SOURCE"  )" && pwd  )"
    SOURCE="$(readlink "$SOURCE")"
    [[ $SOURCE != /*  ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE"  )" && pwd  )"

#这里可替换为你自己的执行程序，其他代码无需更改
APP_NAME=xxl-job-admin-1.0.0-SNAPSHOT.jar
PARAMS="-server -Xmx4096m -Duser.timezone=GMT+08"
SPRING_BOOT_PARAMS="--spring.config.location=${DIR}/../config/application.properties --logging.config=../config/logback.xml"
#JARDR=$DIR'/../services/'$APP_NAME
JARDR=$DIR'/../'$APP_NAME

#echo $JARDR
#cd `dirname $0`
#使用说明，用来提示输入参数

function usage() {
    echo "Usage: sh 执行脚本.sh [start|stop|restart|status]"
    exit 1
}
#检查程序是否在运行
function is_exist(){
  pid=`ps -ef|grep $APP_NAME|grep -v grep|awk '{print $2}' `
  #如果不存在返回1，存在返回0
  if [ -z "${pid}" ]; then
   return 1
  else
    return 0
  fi
}

#启动方法
function start(){
  is_exist
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is already running! pid is ${pid}"
  else
    nohup java $PARAMS -jar $JARDR $SPRING_BOOT_PARAMS > /dev/null 2>&1 &
    echo "${APP_NAME} is start success!"
  fi
}
 
#停止方法
function stop(){
  is_exist
  if [ $? -eq "0" ]; then
    kill -9 $pid
    echo "${APP_NAME} is stoped!"
  else
    echo "${APP_NAME} is not running!"
  fi
}
 
#输出运行状态
function status(){
  is_exist
  if [ $? -eq "0" ]; then
    echo "${APP_NAME} is running! pid is ${pid}"
  else
    echo "${APP_NAME} is not running!"
  fi
}
 
#重启
function restart(){
  stop
  start
}
 
#根据输入参数，选择执行对应方法，不输入则执行使用说明
case "$1" in
  "start")
    start
    ;;
  "stop")
    stop
    ;;
  "status")
    status
    ;;
  "restart")
    restart
    ;;
  *)
    usage
    ;;
esac

#!/bin/bash
# 2011-03-16 Sebastian Rockel
# Start Player planner servers
# Multiple robots scenario

player -p 6666 planner_6666.cfg &
player -p 6668 planner_6668.cfg &
player -p 6670 planner_6670.cfg &

sleep 2

#pOption="-n -o -t 1"

#ping $pOption tams66
#case $? in
  #0)  host1="localhost:6672"
#esac
#ping $pOption tams49
#case $? in
  #0)  host3="localhost:6676"
#esac

#playernav localhost:6665 localhost:6666 localhost:6668 localhost:6670 &
playernav localhost:6665 localhost:6666 localhost:6668 localhost:6670 \
  localhost:6672 \
  localhost:6676 \
  &

  #localhost:6674 \
  #$host1 \
  #$host3 \

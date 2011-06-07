#!/bin/bash
# 2011-03-16 Sebastian Rockel
# Start Player planner servers
# Multiple robots scenario

player -p 6666 planner_6666.cfg &
player -p 6668 planner_6668.cfg &
player -p 6670 planner_6670.cfg &

sleep 2

playernav localhost:6665 localhost:6666 localhost:6668 localhost:6670 &

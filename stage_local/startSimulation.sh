#!/bin/bash
# 2011-03-30 Sebastian Rockel
# Starts Stage simulation with robots

killall player
killall player
killall player
killall playernav


player uhh1.cfg &
#player -p 6600 uhhsimu1.cfg &

sleep 3

./startPlanner.sh &

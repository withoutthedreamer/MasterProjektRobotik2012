#!/bin/bash
# 2011-03-18 Sebastian Rockel
# Start Scale scenario

player scale.cfg &
player -p 6667 scale_laser.cfg &
player -p 6666 planner_scale.cfg &

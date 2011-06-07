#!/bin/bash

tar -czvf PlayerNav.tar.gz \
  startSimulation.sh \
  startPlanner.sh \
  planner_6666.cfg \
  planner_6668.cfg \
  planner_6670.cfg \
  uhh1.cfg \
  uhh1.world \
  urgr.inc \
  utm30lx.cfg \
  utm30lx.inc \
  map.inc \
  platte.inc \
  laptop.inc \
  devices.inc \
  pioneer.inc \
  uoa_robotics_lab_models.inc \
  bitmaps/tams_compl_red_map.png

exit 0

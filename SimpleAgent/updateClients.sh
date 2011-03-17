#!/bin/bash
# 2011-03-17 Sebastian Rockel
# Distribute uptodate player config to clients

scp -r player 8rockel@tams50:robotcolla/SimpleAgent/
scp -r player demo@tams66:/tmp/simpleagent/
scp -r player demo@tams67:/tmp/simpleagent/
scp -r player demo@134.100.13.163:/tmp/simpleagent/

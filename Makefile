# Makefile for the Pioneer Projekt
# Shall be compatible with Uni Network and my local Mac!
# V 0.9
# by Sebastian Rockel
# 2009-12-11
# ++++++++++++++++++++++++++++++++++++++++++++++++++++++
#
CC      = g++
CTAGS   = ctags
TAGFILE = tags
TARGET  = wallfollow
SRCS    = ${TARGET:=.cpp}
OBJS    = ${SRCS:.cpp=.obj}
INC     = include
DEP     = ${SRCS} ${INC}/${SRCS:.cpp=.h} Makefile
TAGSRCS = `pkg-config --cflags playerc++ | sed -e 's/-I//g' | sed -e 's/ .*//g'`

CFLAGSSTD=-pg    \
          -g3    \
          -ggdb    \
          -funit-at-a-time \
          -Wall  \
          -Wcast-align\
          -Waggregate-return \
          -Wcast-qual\
          -Wcomment\
          -Wno-deprecated-declarations\
          -Wdisabled-optimization\
          -Wreturn-type -Wfatal-errors\
          -Wunused
CFLAGSPL= `pkg-config --cflags playerc++`
CFLAGSCV= `pkg-config --cflags opencv`

LIBSPL  = `pkg-config --libs playerc++`
LIBSCV  = `pkg-config --libs opencv`\
          -ldc1394 -lraw1394 -ldc1394_control

.PHONY: all cam clean player playerp view run tag doc docclean sync archive

all:
	@echo
	@echo "make wallfollow\t-- Wallfollow compilation"
	@echo "make cam\t-- Wallfollow with opencv and cam compilation"
	@echo "make clean\t-- Clean objects"
	@echo "make player\t-- Start the player server and stage simulation"
	@echo "make playerp\t-- Start the player server on real pioneer"
	@echo "make view\t-- Start playerv for sensor data"
	@echo "make slam LOGFILE=<logfile>\t-- Start pmaptest creating a grid map"
	@echo "make debug\t-- Start debugger ddd with wallfollow"
	@echo "make tag\t-- Create tags for VIM"
	@echo "make doc\t-- Create doxygen manual for wallfollowing program"
	@echo "make docclean\t-- Clean doxygen manual and files"
	@echo "make sync\t-- Sync mandatory wallfollowing files onto robot laptop"
	@echo "make archive\t-- Create a zip archive from mandatory wallfollowing files"
	@echo

${TARGET}: ${DEP}
	${CC} -o ${TARGET} -I${INC} ${CFLAGSSTD} ${CFLAGSPL} ${SRCS} ${LIBSPL} -U OPENCV

cam: ${DEP}
	${CC} -o ${TARGET} -I${INC} ${CFLAGSSTD} ${CFLAGSPL} ${CFLAGSCV} ${SRCS} ${LIBSPL} ${LIBSCV} -D OPENCV

clean:
	rm -f ${TARGET} ${TAGFILE}
	rm -f *.out
	rm -f *.tgz
	rm -fr *.dSYM

player:
	./start uhh wallfollow # Start the player server and stage simulation

playerp:
	./start pioneer wallfollow  # Start the player server on real pioneer

view:
	playerv -p 6665 --position2d:0 --sonar:0 --ranger:0

slam:
	pmaptest --num_samples 100 --grid_width 16 --grid_height 16 --grid_scale 0.08 --laser_x 0.13 --robot_x -7 --robot_y -7 --robot_rot 90 ${LOGFILE}

debug:
	./start uhh
	ddd -d ${TAGSRCS} ${TARGET} &

tag:
	${CTAGS} -f ${TAGFILE} -R ${TAGSRCS}

doc:
	./makedoc

docclean:
	rm -fr doc/doxygen/*

sync:
	./clone -s

archive:
	./clone -a

# makefile Lab4

.SUFFIXES: .class .java

PACK=Lab4
TARG=output
JC=javac -g
JM=java -ea -jar
JR=jar -cfe

OBJ=$(PACK)\CharSet.class $(PACK)\Main.class

.java.class:
	$(JC) $<

all: build run clean

clean:
	for %f in ($(OBJ)) do del %f
	del $(TARG).jar

run:
	$(JM) $(TARG).jar

build:	$(OBJ)
	$(JR) $(TARG).jar $(PACK).Main $(OBJ)

$(PACK)\CharSet.class: $(PACK)\CharSet.java makefile

$(PACK)\Main.class: $(PACK)\Main.java makefile

# eof makefile Lab4
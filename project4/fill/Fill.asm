// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.

(loop)
@24576
D=M
@white
D;JEQ
//detrmine the color
@color
M=-1
(continue)
@24575
D=A
@stop
//set stop to be last register of screen
M=D
@16384
//set the first register of screen
D=A
@base
M=D
@color
D=M
@base
A=M
M=D
(loopblack)
//loop for painting the screen
@base
//incremate the adress of screen
M=M+1
@color
D=M
@base
//paint
A=M
M=D
D=A
@stop
//if base is the end of ram screen
D=M-D
@loop
D;JEQ
@loopblack
//paint next register 
0;JEQ
(white)
//detrmine the color
@color
M=0
@continue
0;JEQ

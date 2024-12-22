// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/4/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.

@R1
D=M
//intiate i to R1
@i
M=D
@R2
M=0
    (loop)
    //adding R0 to R2 i times
        @i
        D=M
        @end
        D;JEQ
        @i
        M=M-1
        @R0
        D=M
        //R2=R2+R0
        @R2
        M=D+M
        @loop
        0;JEQ
(end)
@end
0;JEQ








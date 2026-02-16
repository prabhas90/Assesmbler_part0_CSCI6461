LOC     10
Data    10
LOC     100
; Load/Store
LDR     1,0,10
STR     1,0,11
LDA     2,0,12
LDX     3,10
STX     3,13

; Arithmetic
AMR     1,0,10
SMR     1,0,10
AIR     1,5
SIR     1,5

; Transfer
JZ      1,0,100
JNE     1,0,100
JMA     0,100
JSR     0,100
RFS     5
SOB     1,0,100
JGE     1,0,100

; Logical
MLT     1,2
DVD     1,2
TRR     1,2
AND     1,2
ORR     1,2
NOT     1

; IO
IN      1,0
OUT     1,0
CHK     1,0

HLT
End

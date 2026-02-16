; Test Case: Fibonacci Number Calculator
; This program calculates the first N Fibonacci numbers
; and demonstrates most assembler features

; Data Section at address 10
LOC     10
Data    5           ; N = 5 (calculate first 5 Fibonacci numbers)
Data    0           ; F(n-2) = 0
Data    1           ; F(n-1) = 1

; Program Section at address 100
LOC     100

; Initialize: Load N and first two Fibonacci numbers
Start:  LDX     1, 10       ; X1 = N (counter)
        LDR     2, 0, 11    ; R2 = F(n-2) = 0
        LDR     3, 0, 12    ; R3 = F(n-1) = 1

; Main Loop: Calculate next Fibonacci number
Loop:   AMR     2, 0, 12    ; R2 = R2 + F(n-1)
        STR     2, 0, 13    ; Store result
        
        ; Swap: F(n-2) = F(n-1), F(n-1) = F(n)
        LDR     2, 0, 12    ; R2 = old F(n-1)
        LDR     3, 0, 13    ; R3 = new F(n)
        STR     3, 0, 12    ; Update F(n-1)
        STR     2, 0, 11    ; Update F(n-2)
        
        ; Decrement counter and check
        SIR     1, 1        ; X1 = X1 - 1
        JNE     1, 0, Loop  ; If X1 != 0, continue loop
        
Done:   HLT                 ; Halt execution
End

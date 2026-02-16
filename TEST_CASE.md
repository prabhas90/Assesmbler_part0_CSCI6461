# Test Case: Fibonacci Number Calculator

## Purpose
This test case demonstrates all major features of the assembler:
- LOC directives for memory placement
- Data directives for constants
- Label definitions and references (forward and backward)
- Arithmetic operations (AMR, SIR)
- Memory operations (LDR, STR, LDX)
- Control flow (JNE, HLT)
- Loop structures

## Test File
See: `test_example.asm`

## How to Run

```bash
java -jar assembler.jar test_example.asm
```

## Expected Output

### Console
```
Assembling test_example.asm...
Starting Pass 1...
Starting Pass 2...
Assembly Complete!
Generated: test_example.lst, test_example.load
```

### Listing File (test_example.lst)

Should show:
- Data section at addresses 000012-000014 (octal 10-12)
- Program section starting at 000144 (octal 100)
- All labels properly resolved:
  - `Start` at 000144
  - `Loop` at 000145
  - `Done` at 000154
- Forward reference: `Loop` label used at line with JNE before it's defined

### Load File (test_example.load)

Should contain address-value pairs in octal format:
```
000012 000005    # N = 5
000013 000000    # F(n-2) = 0
000014 000001    # F(n-1) = 1
000144 412012    # LDX 1,10
000145 022013    # LDR 2,0,11
...
```

## What This Tests

### Pass 1 Features
- ✓ Symbol table construction
- ✓ Location counter management
- ✓ LOC directive handling
- ✓ Label collection (Start, Loop, Done)

### Pass 2 Features
- ✓ Instruction encoding
- ✓ Label resolution (forward reference: JNE to Loop)
- ✓ Backward reference resolution
- ✓ Data directive encoding
- ✓ Proper octal formatting (6 digits)

### Instruction Types Tested
- **Load/Store**: LDR, STR, LDX
- **Arithmetic**: AMR, SIR
- **Control Flow**: JNE, HLT
- **Directives**: LOC, Data, End

## Manual Verification

Check that:
1. **Address 000012-000014** contain the data values (5, 0, 1)
2. **Address 000144** is the first instruction (Start label)
3. **JNE instruction** correctly references the Loop address
4. **All opcodes** match the ISA specification
5. **Octal values** are all 6 digits

## Expected Behavior (If Run on Simulator)

When executed on a CSCI 6461 simulator, this program would:
1. Load N=5 into index register X1
2. Initialize F(n-2)=0, F(n-1)=1
3. Loop 5 times, calculating Fibonacci numbers
4. Store results in memory
5. Halt when counter reaches 0

The first 5 Fibonacci numbers: 0, 1, 1, 2, 3, 5

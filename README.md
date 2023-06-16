# @Date Kotlin Multi Modules Project

[![License](https://img.shields.io/badge/License-ISC-blue.svg)](https://opensource.org/licenses/ISC)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.8.0-blue.svg)](https://kotlinlang.org/)
[![Gradle](https://img.shields.io/badge/Gradle-7.4.2-blue.svg)](https://gradle.org/)

This project is a multi modules for @Date. The poject includes the following modules:
- @Date Kotlin Library
- @Date Kotlin Console Application
- @Date Kotlin Web Api Application

## @Date Kotlin Library (at-date-lib)
### Overview
This library does encode and decode Moment and Period notations using the @Date format.

It provides Models for AtDate, Moment and Period. It also provides a set of functions to encode and decode Moment and Period notations.

### Usage
It's used by @Date Kotlin Console Application (at-date-console) and @Date Kotlin Web Api (at-date-api).

### Examples
```kotlin
// Encode AtDate notation into Moment class
val atDate: AtDate = encode("@2019-05-05 { d:1 }@")
println(atDate) // Moment(rangeLevel=Level1, resolutionLevel=Level0, zoneLevel=Level0, accuracy=Start, leapSecondsFlag=0, date=1009, time=null, zone=null, plusLeapSeconds=null, minusLeapSeconds=null)
```

### Future Work
- Publish it to Maven Central.
- Cover Arithmetic and Logical operations.
- Encode/decode date-time with level or resolution above 15: The code in Kotlin uses a variable of kind ULong which is only 8 bytes long. A different kind of coding should be used, maybe Array of UByte.
- Enhance Unit test in implementation.

## @Date Kotlin Console Application (at-date-console)
### Overview
This application does encode and decode Moment and Period notations to/from bits representation using c/java/kotlin hexadecimal format.

### Usage
To use the application, follow these steps:

1. Clone the repository or download the source code.
2. Open the project in your preferred Kotlin IDE as Gradle project.
3. Locate the `Main.kt` file in the `com.amerharb.atdate` package.
4. Run the `Main` class.
5. Enter the Moment or Period notation or payload (bits representation) to encode or decode.
    - if you start with `@` then the application will encode the notation into payload (bits representation).
    ```shell
   > @2019-05-05@
   Hex: 0xc007e2
   Bin: 0b110000000000011111100010
   >
    ```
    
    - if you start with `0x` then the application will decode payload into @Date notation.  
    ```shell
   > 0xc007e2
   Notation: @2019-05-05 { d:1 t:0 z:0 a:s l:0-0 }@
   >
    ```

### Examples
```shell
@Date
input: 
enter @...@ to encode, 0x... to decode or Q to Quit
>@2019-05-05@
Hex: 0xc007e2
Bin: 0b110000000000011111100010
>0xc007e2
Notation: @2019-05-05 { d:1 t:0 z:0 a:s l:0-0 }@
>@2019-05-05T19:53:00+02:00 {d:1 t:5 a:s l:0-0 z:1}@
Hex: 0xd607e3179c10
Bin: 0b110101100000011111100011000101111001110000010000
>@P-tp@
Hex: 0xa0
Bin: 0b10100000
>@P+1D { d:1 t:0 l:0-0 }@
Hex: 0x880002
Bin: 0b100010000000000000000010
>0x880002
Notation: @P+1D { d:1 t:0 l:0-0 }@
>0xa0
Notation: @P-tp@
>Q
Exiting with status 0

Process finished with exit code 0
```

### Future Work
- Cover Arithmetic and Logical operations when Lib support it.
- Website to demonstrate the design.

## @Date Kotlin Web Api (at-date-api)
### Overview
Provide endpoints that encode and decode Moment and Period notations to/from hexadecimal or base64.

### endpoints
- `GET /encode/{notation}` to encode Moment or Period notation to hexadecimal.
it returns:
``` 
hex: 0x......
base64: ......
```
example:
``` HTTP
GET /encode/@2019-05-05@

hex: 0xc007e2
base64: wAfi
```

- `GET /encode/{notation}/hex` to encode Moment or Period notation to hex.

example:
``` HTTP
GET /encode/@2019-05-05@/hex

0xc007e2
```

- `GET /encode/{notation}/base64` to encode Moment or Period notation to base64.

example:
``` HTTP
GET /encode/@2019-05-05@/base64

wAfi
```

- `GET /decode/hex/{hex}` to decode hexadecimal to Moment or Period notation.

example:
``` HTTP
GET /decode/hex/0xc007e2

@2019-05-05 { d:1 t:0 z:0 a:s l:0-0 }@
```

- `GET /decode/base64/{base64}` to decode base64 to Moment or Period notation.

example:
``` HTTP
GET /decode/base64/wAfi

@2019-05-05 { d:1 t:0 z:0 a:s l:0-0 }@
```

### Future Work
- Support binary.

## Future Work
- Website to demonstrate the design.

## License
This project is licensed under the [ISC License](https://opensource.org/licenses/ISC).

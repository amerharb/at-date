# @Date Kotlin Console Application

This is a Kotlin console application that encodes and decodes Moment and Period notations using the @Date format.

## Overview

The @Date format is a custom notation for representing date-time (Moment and Period). This application allows you to encode a given Moment or Period into @Date bits representation and decode bits representation of @Date notation into its corresponding Moment or Period notation.

## Usage

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
## Unit Tests

The application includes unit tests to verify the correctness of the encoding and decoding operations. These tests ensure that the application produces the expected output for various input scenarios.

To run the unit tests, follow these steps:
1. Open the project in your preferred Kotlin IDE.
2. Locate the `TestMain.kt` file in the `com.amerharb.atdate` package.
3. Run the `TestMain` class or execute the unit tests individually to validate the encoding and decoding functionality.

## Examples
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

## License

This project is licensed under the [ISC License](LICENSE).

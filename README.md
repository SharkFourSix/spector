# What

Spector - Lightweight and extensible file content type detection library.

# Why

- Because most file detectors in Java (looking at you Tika) are too heavy 
    and you end up with very obese uber JARs, a polluted classpath 
    and dependency tree.

# How

- Spector works by inspecting file byte sequences, called blocks, which 
    make up file signatures.

- File signatures can be added/loaded as needed using runtime API or
    through SPI.

# Who

- If you deal with a lot of file uploads and want to be sure the 
    right type of files are being uploaded to your server.        
    
# Examples

##### Maven

Add JitPack to include the core API dependency

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Then

```xml
<dependency>
    <groupId>lib.gintec_rdl</groupId>
    <artifactId>spector</artifactId>
    <version>{{ version }}</version>
</dependency>
```

The core API library does not come with any signatures only
    the API. You can include a number of different file signature 
    providers as you see fit.
    
There are currently two other related provider libraries that I will 
    be adding more signatures to:
    
- [https://github.com/SharkFourSix/spector-document-file-signatures](https://github.com/SharkFourSix/spector-document-file-signatures)
- [https://github.com/SharkFourSix/spector-image-file-signatures](https://github.com/SharkFourSix/spector-image-file-signatures) 

###### Image File Signatures

Include this library for detecting image files

```xml
<dependency>
    <groupId>lib.gintec_rdl.spector</groupId>
    <artifactId>image-file-signatures</artifactId>
    <version>{{ version }}</version>
</dependency>
```

###### Document File Signatures 

Include this library for detecting document files

```xml
<dependency>
    <groupId>lib.gintec_rdl.spector</groupId>
    <artifactId>document-file-signatures</artifactId>
    <version>{{ version }}</version>
</dependency>
```

##### Usage

```java
import lib.gintec_rdl.spector.Spector;
import lib.gintec_rdl.spector.TypeInfo;

class Example {
    public static void main(String[] args) {
        TypeInfo typeInfo = Spector.inspect("file.png");
        if (typeInfo != null) {
            System.out.println("Type: " + typeInfo.getMime());
            System.out.println("Extension: " + typeInfo.getExtension());
        } else {
            System.out.println("Unsupported file type");
        }
    }
}
```


##### Extending the API

There's not much to add apart from loading file signatures 
    from various sources. There are two ways to do this. The first thing is to implement 
    [FileSignatureProvider](src/main/java/lib/gintec_rdl/spector/FileSignatureProvider.java), 
    then:
    
1.  Either directly add the provider to Spector through `Spector.addProviders(new MyProvider())`
    or
2. Using the SPI API. If using the SPI API, make sure you 
    call `System.setProperty("spector.autoloadProviders", "true")` to have Spector 
    automatically load providers before calling any other Spector method.
    
    SPI auto loading is disabled by default due to security reasons.    
3. There's already a provider for loading signatures from resources called `ResourceFileSignatureProvider`

# Signature Files


Signature files are JSON files with the following structure:

```json
{
  "name": "PNG",
  "ext": "png",
  "mime": "image/png",
  "blocks": [
    {
      "name": "PNG Header",
      "offset": 0,
      "bytes": "89504e470d0a1a0a"
    }
  ]
}
```

The _bytes_ property contains hex encoded byte values of the byte 
    sequence to look for, at the specified offset, in order to identify 
    the file as PNG file.
    
```json
{
  "name": "JPEG",
  "ext": "jpg",
  "mime": "image/jpeg",
  "blocks": [
    {
      "name": "JPEG Header",
      "offset": 0,
      "bytes": "FFD8FF"
    },
    {
      "offset": 2,
      "name": "JPEG Trailer",
      "seek": "End",
      "bytes": "FFD9"
    }
  ]
}
```    
Above signature is for a JPEG file. The _seek_ property in the last 
    block indicates where to start scanning for the the bytes as it 
    relates to the file offset.
    
If the _seek_ property is not defined, the default value is `Begin`,
    indicating the file offset should be adjusted relative to the 
    beginning of the file. Valid values are `Current`, `Begin`, and `End`
    
##### Where can I get file signatures?

- I will be adding more file signature providers, categorically, 
    i.e `spector-document-signatures`, `spector-image-signatures` to 
    avoid inundation.
    
- You could also implement your own and load locally from disk. 
    You can find file structures from various places on the the 
    internet and create your own signature files. Feel free to 
    contribute here as well.
    
Spector uses SLF4J so configure as required

##### Wildcard Specifier

In cases where the need to skip certain bytes arises, a wildcard `?` can be specified instead. 
Wildcards will match any encountered value, effectively skipping that value.

`??` will match a whole octet and a single `?` will only match one half of the octet, depending on the specified position.
For instance, `1?` will match any byte value within the range of `10` - `1F`. Likewise `?1` will match values 
in range `01 - F1`.

Putting it all together, a signature  with wildcard might look like this:

```json
{
  "name": "PNG",
  "ext": "png",
  "mime": "image/png",
  "blocks": [
    {
      "name": "PNG Header",
      "offset": 0,
      "bytes": "89504e4??d0a1a0?"
    }
  ]
}
```
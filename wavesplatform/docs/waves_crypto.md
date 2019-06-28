# Mobile Waves Crypto

Collection of functions to work with Waves basic types and crypto primitives used by Waves.

## Main definitions

###### Seed or seed-phrase
list of words which store all the information needed to recover a private key. Usually it generate from prepared 2048 words list by BIP39 mnemonic code and look like "same era morning alarm must faculty wealth sister wise myth install party fabric unaware depart" but can be any string with any length.

###### Private key or secret key
it's a string or byte array is paired with a public key to set off algorithms for text encryption and decryption. It is created as part of public key cryptography during asymmetric-key encryption and used to decrypt and transform a message to a readable format. Public and private keys are paired for secure communication. It's give access to account! Loss or publicity of it can lead to loss of control of the funds registered to the blockchain address.

###### Public-key
t's a string or byte array, open or public part of pair public and private keys. Public key can be compute from private key only by owner. And only owner can create signatures by his private key. Everyone who have signature can verify it and owner by public key. Everyone can create address in blockchain by public key with Base58 encode.

###### Base64
binary-to-text and reverse encoding function function used to represent binary data in an ASCII string format by translating it into a radix-64 representation. The implementation uses A–Z, a–z, and 0–9 for the first 62 values and '+', '/' characters. In Waves blockchain it often uses for different attachments and aliases.

###### Base58
binary-to-text and reverse encoding function used to represent large integers as alphanumeric text. Compared to Base64 the following similar-looking letters are omitted: * 0 (zero), O (capital o), I (capital i) and l (lower case L) as well as the non-alphanumeric characters '+', '/'. 

Crypto-methods blake2b, keccak and sha256 needs for creation private key from seed.

## Methods list:

* blake2b(input: Bytes): Bytes
* keccak(input: Bytes): Bytes
* sha256(input: Bytes): Bytes
* base58encode(input: Bytes): String
* base58decode(input: String): Bytes
* base64encode(input: Bytes): String
* base64decode(input: String): Bytes
* keyPair(seed: Seed): KeyPair
* publicKey(seed: Seed): PublicKey
* privateKey(seed: Seed): PrivateKey
* addressByPublicKey(publicKey: PublicKey, chainId: String?): Address
* addressBySeed(seed: Seed, chainId: String?): Address
* randomSeed(): Seed
* signBytesWithPrivateKey(bytes: Bytes, privateKey: PrivateKey): Bytes
* signBytesWithSeed(bytes: Bytes, seed: Seed): Bytes
* verifySignature(publicKey: PublicKey, bytes: Bytes, signature: Bytes): Boolean
* verifyPublicKey(publicKey: PublicKey): Boolean
* verifyAddress(address: Address, chainId: String, publicKey: PublicKey): Boolean

##Interface:
```java
/**
 * Collection of functions to work with Waves basic types and crypto primitives used by Waves
 */
interface WavesCrypto {

    /**
     * BLAKE2 are cryptographic hash function
     *
     * @param input byte array of input data
     * @return byte array of hash values
     */
    fun blake2b(input: Bytes): Bytes

    /**
     * Keccak are secure hash algorithm
     *
     * @param input byte array of input data
     * @return byte array of hash values
     */
    fun keccak(input: Bytes): Bytes

    /**
     * SHA-256 are cryptographic hash function
     *
     * @param input byte array of input data
     * @return byte array of hash values
     */
    fun sha256(input: Bytes): Bytes

    /**
     * Base58 binary-to-text encoding function used to represent large integers as alphanumeric text.
     * Compared to Base64 like in base64encode(), the following similar-looking letters are omitted:
     * 0 (zero), O (capital o), I (capital i) and l (lower case L) as well
     * as the non-alphanumeric characters + (plus) and / (slash)
     *
     * @param input byte array containing binary data to encode
     * @return encoded string containing Base58 characters
     */
    fun base58encode(input: Bytes): String

    /**
     * Base58 text-to-binary function used to restore data encoded by Base58,
     * reverse of base58encode()
     *
     * @param input encoded Base58 string
     * @return decoded byte array
     */
    fun base58decode(input: String): Bytes

    /**
     *  Base64 binary-to-text encoding function used to represent binary data in an ASCII
     *  string format by translating it into a radix-64 representation.
     *  The implementation uses A–Z, a–z, and 0–9 for the first 62 values and '+', '/'
     *
     *  @param input byte array containing binary data to encode.
     *  @return String containing Base64 characters
     */
    fun base64encode(input: Bytes): String

    /**
     * Base64 text-to-binary function used to restore data encoded by Base64,
     * reverse of base64encode()
     *
     * @param input encoded Base64 string
     * @return decoded byte array
     */
    fun base64decode(input: String): Bytes

    /**
     * @return a public and private key-pair by seed-phrase
     */
    fun keyPair(seed: Seed): KeyPair

    /**
     * @return a public key as String by seed-phrase
     */
    fun publicKey(seed: Seed): PublicKey

    /**
     * @return a private key as String by seed-phrase
     */
    fun privateKey(seed: Seed): PrivateKey

    /**
     * @return a new generated Waves address as String from the publicKey and chainId
     */
    fun addressByPublicKey(publicKey: PublicKey, chainId: String?): Address

    /**
     * @return a new generated Waves address as String from the seed-phrase
     */
    fun addressBySeed(seed: Seed, chainId: String?): Address

    /**
     * Random Seed-phrase generator from 2048 prepared words.
     * It's a list of words which store all the information needed to recover a private key
     * @return a new randomly generated BIP39 seed-phrase
     */
    fun randomSeed(): Seed

    /**
     * @param privateKey is a key to an address that gives access
     * to the management of the tokens on that address as String.
     * It is string encoded by Base58 from byte array.
     * @return signature for the bytes by privateKey as byte array
     */
    fun signBytesWithPrivateKey(bytes: Bytes, privateKey: PrivateKey): Bytes

    /**
     * @return signature for the bytes by seed-phrase as byte array
     */
    fun signBytesWithSeed(bytes: Bytes, seed: Seed): Bytes

    /**
     * @return true if signature is a valid signature of bytes by publicKey
     */
    fun verifySignature(publicKey: PublicKey, bytes: Bytes, signature: Bytes): Boolean

    /**
     * @return true if publicKey is a valid public key
     */
    fun verifyPublicKey(publicKey: PublicKey): Boolean

    /**
     * Checks address for a valid by optional chainId and publicKey params
     * If params non null it's will be checked.
     * @param address a unique identifier of an account on the Waves blockchain
     * @param chainId it's id of blockchain network 'W' for production and 'T' for test net
     * @param publicKey
     * @return true if address is a valid Waves address for optional chainId and publicKey
     */
    fun verifyAddress(address: Address, chainId: String? = null, publicKey: PublicKey? = null): Boolean
}
```
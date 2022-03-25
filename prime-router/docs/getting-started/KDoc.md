## KDoc usage

Kotlin's documentation generation language is called *KDoc*.
Basic information can be found [here](https://kotlinlang.org/docs/kotlin-doc.html).
There are three things to keep in mind when writing/editing KDoc comments in your code.

### KDoc comments look *almost* like standard comments
```
/**
 * This is a KDoc comment
 */

/*
 * This is NOT A KDOC COMMENT
 */
 ```
You need to include the double asterisk on the top line

### You should include a class description as the first block

>By convention, the first paragraph of the documentation text (the block of text until the first blank line) is the summary description of the element, and the following text is the detailed description.
Every block tag begins on a new line and starts with the @ character.


### Your comments should have meaning
A lot of the documentation for KDOC includes obvious language:
```
/**
 * A class for user objects
 *
 * This class handles User objects
 *
 * @property name The name of the user
 * @property statPin the statPin of the user
 * @property expDate the expDate of the user
 */
```
From these descriptions, we learn nothing more than what the names convey.  

```
/**
 * User objects
 *
 * Users have minimal permissions, and are a base for all other roles
 *
 * @property name The name of the user
 * @property statPin static password (PIN) for bailing out users
 * @property expDate exploration atart date
 */
```
When adding KDoc comments to other people's code, you may see a property that you know nothing about.  It is very tempting to fill in with this obvipous language.
```
 * @property landFill additional spacing for landscape orientation
 * @property kVortex the kVortex value of this document
 * @property argPath the local location of the argList

```
Now, nobody knows what kVortex is.  And because there is a description, it's not obvious that this is a problem. The better solution is to leave the description missing from the property.
```
 * @property landFill additional spacing for landscape orientation
 * @property kVortex
 * @property argPath the local location of the argList

```
In this way, someone who knows what this value is can see that the description is missing and fill it in.  Better still is to contact the person who wrote the code and ask what the property does.

##Finally:
Be clear and concise when describing a block tag (like `@property`)
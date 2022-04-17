## KDoc usage

Kotlin's documentation generation language is called *KDoc*.
Basic information can be found at [https://kotlinlang.org/docs/kotlin-doc.html](https://kotlinlang.org/docs/kotlin-doc.html).
There are a few things to keep in mind when writing/editing KDoc comments in your code.

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

### Other tag block that you should include

If you are documenting a class, you should document the properties of the class with ```@property```
If you are documenting a method, you should include an ```@parameter``` for each parameter.  Also,
include an ```@return``` tag block to describe what is returned by the method.
#### Here is an example for a word-counting method, ```wordCount()```
```
/**
 * returns the word count from a string
 *
 * @param inStr the target string for counting words
 * @return the number of words in inStr (0 if null)
 */
```
### Your comments should have meaning
A lot of the example documentation for KDoc on the web includes "obvious language":
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
From these examples, we are encouraged state nothing more than what the names convey.  We should go beyond this commenting style, and convey meaningful information:

```
/**
 * User objects
 *
 * Users have minimal permissions, and are a base for all other roles
 *
 * @property name The name of the user
 * @property statPin static password (PIN) for bailing out users
 * @property expDate exploration start date
 */
```
When adding KDoc comments to other people's code, you may see a property that you know nothing about.  It is very tempting to fill in with this "obvious language."
```
 * @property landFill additional spacing for landscape orientation
 * @property kVortex the kVortex value of this document
 * @property argPath the local location of the argList

```
Now, nobody knows what kVortex is.  And because some kind of description exists for kVortex, it's not clear that there is a problem. The better solution is to leave the description missing from the property.
```
 * @property landFill additional spacing for landscape orientation
 * @property kVortex
 * @property argPath the local location of the argList

```
In this way, someone who knows what this value is can see that the description is missing and fill it in.  Better still is to contact the person who wrote the code and ask what the property does.


## Always:
Be clear and concise when describing a block tag (like `@property`)

## Try not to leave out important KDoc block tags.
The full list can be found at [https://kotlinlang.org/docs/kotlin-doc.html#block-tags](https://kotlinlang.org/docs/kotlin-doc.html#block-tags)
For instance, if your method returns a value, include an `@return` block tag.  As we get to know the block tags, we can help each other to include them via the PR review.

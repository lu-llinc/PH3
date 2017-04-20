# PH3 - Phunky Phrase Phinder #

*Arvid Halma - Centre for Innovation - Leiden University*

This library helps you to classify a text with labels/tags by creating matching rules in a DSL.
A rule set is a declarative list of rules that looks as follows:

```
label_1 = expression_1
label_2 = expression_2
```

for instance, when the rules 

```
fruit = (apple OR pear) AND NOT juice
food = bread OR rice OR #fruit
```

are evaluated against the text `Do you like rice?`, the `food` label will be found to be applicable.

When the same rule set is evaluated against `Apples is what I like`, both `food` and `fruit` are returned.

In other words, this tool will efficiently scan for sequences within a given text. 

Where regular expressions work on a character level, this pattern matcher works on a word level.
More high-level matching incentives can then be used:
 - finding stemmed/normalized occurrences, where `'juice'` = `'juicy'` = `'JUICES'`
 - finding word sequences with wildcards (where order is important): `the_?_apple` matches "the red apple", but not "the apple" or "the super red apple".
 - compose hierarchical rules, e.g. where `#fruit` in the `food` rule refers to the `fruit` expression
 
### Internationalization ###
The rule set can in principle be used for any unicode character sequences. That means it can be used from English to Arabic

### Speed ###
There is a query language that is compiled to a reusable form that can be used for efficient occurrence checking. It seems millions of patterns can be matched per second, although it is not profiled in a sophisticated way. Have a look at the test folder.


## Defining Pattern expressions ##  

### Words ###
There are two types of word literals:

 1. Single quoted words are matched *in-exact*. That is: case insensitive, UTF normalized, accents and unusual characters are removed and words are stemmed. So, `'Cost-effective'` matches `'costeffectiveness'`.
 2. Double quoted words are matched *exact*. So, `"Cost-effective"` won"t match `"cost-effective"` or `"Cost-effectiveness"` or `"costeffectiveness"`

You can omit the single quotes when words only consist of alpha-numeric character, so  `hello` equals `'hello'` 

### Logical operators ###

Use logical expressions to define more fine grained queries. The following operators are defined.
 
 * `AND`, `&`: a conjunction of two words/expressions.
 * `OR`, `|`: a disjunction of two words/expressions.
 * `NOT`, `-`: a negation of a word/expression.
    
Parenthesis can be used to define/clarify evaluation order.

example:

`('Cost-effective' AND NOT free) OR cheap`

or, when you prefer using the operator symbols:

`('Cost-effective' & -free) | cheap`

### Sequences ###

In case you want to make sure multiple words are matched only when they are adjacent, the underscore can be used.

example:

`the_big_book` is matched in `He finds the BIG book nice`, but *not* in `The book is big`

Note that this is more specific than when using using a conjunction:

`the & big & book` is matched in `He finds the BIG book nice`, and *also* in  `The book is big`

### Wild cards ###

When using sequences, you can use wild-cards to skip a number of words, but make sure the order is taken into account.
  
* `?` matches a single word. `the_?_book` is matched in `He finds the BIG book nice`.
* `+` matches a one or more words. `the_+_book` is matched in `He finds the BIG book nice` and in `the very big book`, but not in `the book`.
* `*` matches a zero or more words. `the_*_book` is matched in `He finds the BIG book nice` and in `the very big book`, and also in `the book`.
 

## Code example ##

`org.c4i.nlp.ph3.match.MatchUtil` is a good start for matching, since it contains several convenience methods.

```
findRange("He finds the BIG book nice", "the_+_book") => [2, 5]
```

Like using regular expressions, reusing a compiled expression will be more efficient when matching it against multiple texts.
This can be done with `compilePattern` from the same class.

```
compilePattern("the_+_book")
```

It will return a `Literal[][]` that can be used in the `contains` and `findRange` methods. The same holds for tokenization of a sentence into words. Then have a look at `textToTokens`.


### String normalization ###

When inexact words should be matched, you can tune how they should be normalized before words are compared.
The methods in `org.c4i.nlp.ph3.match.MatchUtil` take a `StringNormalizer` argument that is applied to the words in the text and in the pattern.
A StringNormalizer essentially wraps a function that consumes a String and yields a new one. 

`org.c4i.nlp.ph3.normalize.StringNormalizers` contains several predefined normalizers, that

* trims words
* lower case all characters
* remove dashes, quotes, etc
* remove accents
* normalizes UTF characters
* Porter stemming

`org.c4i.nlp.ph3.normalize.StringNormalizers.DEFAULT` and `DEFAULT_STEMMED` are two instances that combine most of the above.

You can also compose them to your own liking.

```
import org.c4i.nlp.ph3.normalize.StringNormalizers.*;
...
findRange("He finds the BIG book nice", "the_+_book", LOWER_CASE.andThen(UNICODE).andThen(NO_ACCENTS))
```
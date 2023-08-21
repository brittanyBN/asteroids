Improvement suggestions:

*Build*

Java 20 is the most recent LTS version of Java, and gradle 7 is not compatible.
I recommend updating to the project to use gradle 8, and checking that the packages
used are also updated. I noticed a few packages currently being used have 
vulnerabilities. 

*Models*

- CloseApproachData class:

    The date is expected have the format "yyyy-MMM-dd hh:mm" but the date actually has the
    format "yyyy-Mmm-dd hh:mm". You cannot say "Mmm", but by specifying that the month abbreviation
    should be interepted using the US locale both uppercase and lowercase month abbreviations will be 
    accepted.
@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MMM-dd hh:mm", locale = "en_US")

- general model observations:

    - Pros:
The models were separated into a separate "model" folder making it easy to find the models.
The models classes are annotated for JSON serialization, which reduces code in other files.

    - Cons:
The class names are inconsistent. Some are plural and others are singular.
The model classes are not normalized. For example, 'id' and 'name' are being duplicated 
in some classes (could use inheritance).
Multiple of the models/methods/fields are not used making it more challenging to understand the code.
It is generally best practice to remove unused methods to keep the code clean and more readable. I would
like to discuss whether these methods should be kept.

*App*

- Constants such as NEO_FEED_URL and API_KEY should be written as constants:
Example: private static final String DEFAULT_API_KEY = "DEMO_KEY";

- ObjectMapper created twice, redundant instance.

- Use String.format for cleaner output.

- Remember "Single Responsibility Principle" and split longer methods up when necessary.

- close Client instance after all invocations for target resource have been performed

*ApproachDetector*

- neos is quite vague. nearEarthObjects would be clearer name.

- Use String.format for cleaner output.

*VicinityComparator*

- It's possible to use Optional.orElse to remove the nested if statements.

- lambda can be replaced with method references

*Task 1*




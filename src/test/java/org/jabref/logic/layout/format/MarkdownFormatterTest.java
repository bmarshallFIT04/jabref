package org.jabref.logic.layout.format;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;


class MarkdownFormatterTest {

    private MarkdownFormatter markdownFormatter;

    @BeforeEach
    void setUp() {
        markdownFormatter = new MarkdownFormatter();
    }

    @Test
    void formatWhenFormattingNullThenThrowsException() {
        Exception exception = assertThrows(NullPointerException.class, () -> markdownFormatter.format(null));
        assertEquals("Field Text should not be null, when handed to formatter", exception.getMessage());
    }

    @Test  // Test to PASS
    public void testInputBold() {
        String userInput = "**this is bold** text from user";
        assertTrue(MarkdownFormatter.hasProperBold(userInput),
                "User input should contain properly formatted bold (**text**).");
    }

    @Test  // Test to PASS
    public void testInputItalic() {
        String userInput = "This is _italic_ text from user";
        assertTrue(MarkdownFormatter.hasProperItalic(userInput),
                "User input should contain properly formatted italic (_text_).");
    }

    @Test  // Test to PASS
    public void testInputStrikethrough() {
        String userInput = "Here is ~~struck through~~ text from user";
        assertTrue(MarkdownFormatter.hasProperStrikethrough(userInput),
                "User input should contain properly formatted strikethrough (~~text~~).");
    }

    @Test  // Test to FAIL
    public void testInvalidBold() {
        String userInput = "**bold text without closing stars";
        assertTrue(MarkdownFormatter.hasProperBold(userInput),
                "Incorrect bold formatting should fail the bold check.");
    }

    private static Stream<Arguments> provideMarkdownAndHtml() {
        return Stream.of(
                Arguments.of("Hello World", "<p>Hello World</p>"),
                Arguments.of("""
                        Markup
                        
                        * list item one
                        * list item two
                        
                         rest
                        """,
                        "<p>Markup</p> <ul> <li>list item one</li> <li>list item two</li> </ul> <p>rest</p>"
                ),
                Arguments.of("""
                        ```
                        Hello World
                        ```
                        """,
                        "<pre><code>Hello World </code></pre>"
                ),
                Arguments.of("""
                       First line
                    
                       Second line
                    
                       ```java
                       String test;
                       ```
                    """,
                    "<p>First line</p> <p>Second line</p> <pre><code class=\"language-java\">String test; </code></pre>"
                ),
                Arguments.of("""
                       Some text.
                       ```javascript
                       let test = "Hello World";
                       ```
                    
                       ```java
                       String test = "Hello World";
                       ```
                       Some more text.
                    """,
                    "<p>Some text.</p> <pre><code class=\"language-javascript\">let test = &quot;Hello World&quot;; " +
                            "</code></pre> <pre><code class=\"language-java\">String test = &quot;Hello World&quot;; " +
                            "</code></pre> <p>Some more text.</p>"
                ),
                Arguments.of("""
                        Some text.
                        
                        ```java
                        int foo = 0;
                        foo = 1;
                        
                        ```
                        """,
                        "<p>Some text.</p> <pre><code class=\"language-java\">int foo = 0; foo = 1;  </code></pre>"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideMarkdownAndHtml")
    void formatWhenFormattingCodeBlockThenReturnsCodeBlockInHtml(String markdown, String expectedHtml) {
        assertEquals(expectedHtml, markdownFormatter.format(markdown));
    }
}

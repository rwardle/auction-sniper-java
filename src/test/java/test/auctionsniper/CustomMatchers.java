package test.auctionsniper;

import org.hamcrest.Matcher;

import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public class CustomMatchers {

    public static <T> T with(Matcher<T> matcher) {
        return argThat(matcher);
    }
}

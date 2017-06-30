package test.auctionsniper;

import auctionsniper.*;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static test.auctionsniper.CustomMatchers.with;

public class SniperLauncherTest {

    private final AuctionHouse auctionHouse = mock(AuctionHouse.class);
    private final AuctionSpy auction = spy(new AuctionSpy());
    private final SniperCollector sniperCollector = mock(SniperCollector.class);
    private final SniperLauncher launcher = new SniperLauncher(auctionHouse, sniperCollector);

    @Test
    public void addsSniperToCollectorAndThenJoinsAuction() {
        String itemId = "item 123";

        when(auctionHouse.auctionFor(itemId)).thenReturn(auction);
        doAnswer(invocation -> {
            verifyAuctionIs("not joined", invocation.getMock());
            return invocation.callRealMethod();
        }).when(auction).addAuctionEventListener(any());
        doAnswer(invocation -> {
            verifyAuctionIs("not joined", auction);
            return null;
        }).when(sniperCollector).addSniper(any());

        launcher.joinAuction(itemId);

        verifyAuctionIs("joined", auction);
        verify(auction).addAuctionEventListener(with(sniperForItem(itemId)));
        verify(sniperCollector).addSniper(with(sniperForItem(itemId)));
    }

    private static void verifyAuctionIs(String state, Object auction) {
        assertThat(((AuctionSpy) auction).state, is(state));
    }

    private static Matcher<AuctionSniper> sniperForItem(String itemId) {
        return new TypeSafeDiagnosingMatcher<AuctionSniper>() {
            @Override
            protected boolean matchesSafely(AuctionSniper item, Description mismatchDescription) {
                return item.getSnapshot().isForSameItemAs(SniperSnapshot.joining(itemId));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a sniper for item ").appendText(itemId);
            }
        };
    }

    private static class AuctionSpy implements Auction {

        String state = "not joined";

        @Override
        public void join() {
            state = "joined";
        }

        @Override
        public void bid(int amount) {
            // no-op
        }

        @Override
        public void addAuctionEventListener(AuctionEventListener listener) {
            // no-op
        }
    }
}

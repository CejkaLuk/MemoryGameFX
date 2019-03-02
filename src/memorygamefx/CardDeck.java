package memorygamefx;

import java.util.ArrayList;
import java.util.Collections;
import javafx.scene.input.MouseEvent;

public final class CardDeck
{
    
    private final ArrayList<Card> cards_in_this_deck = new ArrayList<>();

    // Number of unique cards, not the total number of cards in deck.
    private final int unique_cards;

    // Number of cards that are not matched and revealed.
    private int num_active_cards_revealed = 0;
    
    private int num_matched_cards = 0;

    // The first card out of a pair of revealed cards.
    private Card first_revealed_card;
    // The second card out of a pair of revealed cards.
    private Card second_revealed_card;

    
    // When a card is clicked.
    void onMouseClicked( Card card )
    {
        // If the now clicked on card is currently unmatched.
        if ( !card.getIsMatched() )
        {
            // If there is a last revealed card, there is also a first revealed card. They didn't match, turn them over.
            if ( second_revealed_card != null )
            {
                first_revealed_card.setFaceDown();
                second_revealed_card.setFaceDown();
                
                first_revealed_card = null;
                second_revealed_card = null;
            }
            
            // Set the now clicked on face up.
            card.setFaceUp();

            // If this is the first card out of a pair revealed.
            if ( num_active_cards_revealed == 0 )
            {
                first_revealed_card = card;
                second_revealed_card = null;
                num_active_cards_revealed = 1; 
            }
            // There already is a card revealed out of a pair AND the already revealed card is not the same as the now clicked card.
            else if ( num_active_cards_revealed == 1 && first_revealed_card != card )
            {
                // The cards have the same ID (they match).
                if ( first_revealed_card.equals(card) )
                {
                    first_revealed_card.setIsMatched(true);
                    card.setIsMatched(true);
                    
                    // Reset the number of ACTIVE revealed cards.
                    num_active_cards_revealed = 0;
                    
                    // Add the two just matched cards into the number of cards already matched.
                    num_matched_cards += 2;
                    
                    // Add the just matched cards to the score of the active player.
                    MemoryGameFX.playerScoreAdd();
                    
                    // If the number of matched cards is equal to the number of cards in total (in the deck).
                    if( num_matched_cards == cards_in_this_deck.size() )
                    {
                        MemoryGameFX.gameWon();
                    }
                }
                // The cards didn't match.
                else
                {
                    // Change the active player.
                    MemoryGameFX.activePlayerChange();
                    
                    // Set the second revealed card (so that it can be turned over, once a new card is clicked).
                    second_revealed_card = card;
                    
                    // Reset the number of active cards revealed.
                    num_active_cards_revealed = 0;
                }
            }
        }
    }

    public CardDeck( int uniq_cards )
    {
        unique_cards = uniq_cards;
        
        // Add all cards to the deck
        for ( int card_ID = 1; card_ID < unique_cards+1; card_ID++ )
        {
            // addCard cannot be used twice, had errors with it, just added two identical cards twice under different names.
            Card card1 = new Card( card_ID );
            card1.setIsMatched(false);

            // Set the mouse click property of the added card.
            card1.setOnMouseClicked( (MouseEvent event) -> { onMouseClicked(card1); });
            addCard( card1 );
            

            Card card2 = new Card( card_ID );
            card2.setIsMatched(false);

            // Set the mouse click property of the added card.
            card2.setOnMouseClicked( (MouseEvent event) -> { onMouseClicked(card2); });
            // Add the card a second time.
            addCard( card2 );
        }
    }

    public void shuffle()
    {
       Collections.shuffle( cards_in_this_deck );
    }

    public void addCard( Card given_card )
    {
        cards_in_this_deck.add( given_card );
    }

    public Card getCard( int card_ID )
    {
        Card card_to_return = cards_in_this_deck.get( card_ID );  
        return card_to_return;  
    }
}
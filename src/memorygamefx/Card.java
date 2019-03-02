package memorygamefx;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Card extends ImageView
{
    
    private final int card_ID ;
    private boolean is_matched;

    Image card_face_image;

    public Card ( int given_card_ID )
    {
         card_ID = given_card_ID;

         card_face_image = ImageStore.card_face_images.get( card_ID );

         // Initially the card is face-down.
         setImage( ImageStore.card_back_image );
    }

    public void setFaceUp()
    {
       setImage(card_face_image );
    }

    public void setFaceDown()
    {
       setImage( ImageStore.card_back_image );
    }
    
    public boolean equals( Card another_card )
    {
       return ( card_ID  ==  another_card.card_ID );
    }

    public boolean getIsMatched()
    {
        return is_matched;
    }
    
    public void setIsMatched(boolean matched)
    {
        is_matched = matched;
        if( is_matched )
            dimOutCard();
    }
    
    // Make matched cards appear dim.
    private void dimOutCard()
    {
        ColorAdjust color_adjust = new ColorAdjust();
        color_adjust.setBrightness( 0.6 );

        this.setEffect( color_adjust ); 
    }
}

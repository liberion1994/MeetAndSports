package space.liberion.meetandsports.appointment;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.firebase.client.Query;

import space.liberion.meetandsports.R;
import space.liberion.meetandsports.appointment.models.ChatMessage;

/**
 * @author greg
 * @since 6/21/13
 *
 * This class is an example of how to use FirebaseListAdapter. It uses the <code>ChatMessage</code> class to encapsulate the
 * data for each individual chat message
 */
public class MessageListAdapter extends FirebaseListAdapter<ChatMessage> {

    // The mUsername for this client. We use this to indicate which messages originated from this user
    private String mUsername;

    public MessageListAdapter(Query ref, Activity activity, int layout, String mUsername) {
        super(ref, ChatMessage.class, layout, activity);
        this.mUsername = mUsername;
    }

    /**
     * Bind an instance of the <code>ChatMessage</code> class to our view. This method is called by <code>FirebaseListAdapter</code>
     * when there is a data change, and we are given an instance of a View that corresponds to the layout that we passed
     * to the constructor, as well as a single <code>ChatMessage</code> instance that represents the current data to bind.
     *
     * @param view A view instance corresponding to the layout we passed to the constructor.
     * @param chatMessage An instance representing the current state of a chatMessage message
     */
    @Override
    protected void populateView(View view, ChatMessage chatMessage) {
        // Map a ChatMessage object to an entry in our listview
        String author = chatMessage.getAuthor();
        TextView authorText = (TextView) view.findViewById(R.id.author);
        authorText.setText(author + ": ");
        // If the message was sent by this user, color it differently
        if (author != null && author.equals(mUsername)) {
            authorText.setTextColor(Color.RED);
        } else {
            authorText.setTextColor(Color.BLUE);
        }
        ((TextView) view.findViewById(R.id.message)).setText(chatMessage.getMessage());
    }
}

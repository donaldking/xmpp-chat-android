package co.uk.tusksolutions.tchat.android.viewHolders;

import android.view.View;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import co.uk.tusksolutions.tchat.android.R;

public class GroupChatToImageViewHolder {

	 
	    public ImageView imagesent;
	    //public ProgressBar bar;

	    public GroupChatToImageViewHolder(View v){
	       
	    imagesent=(ImageView) v.findViewById(R.id.imageSent);
	   // bar=(ProgressBar)v.findViewById(R.id.upload_progress);
	      
	    }
}

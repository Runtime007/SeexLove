package com.chat.seecolove.view.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.chat.seecolove.R;
import com.chat.seecolove.widget.PhotoViewAttacher;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 */
public class ImageDetailFragment extends Fragment {
    private String mImageUrl;

    public static ImageDetailFragment newInstance(String imageUrl) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        PhotoDraweeView imageView = (PhotoDraweeView) inflater.inflate(R.layout.browse_item, null);
        if(mImageUrl.contains("file://")){
            ImageRequest imageRequest =
                    ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://"+mImageUrl))

                            .build();
            DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setOldController(imageView.getController())
                    .setAutoPlayAnimations(true)
                    .build();
            imageView.setController(draweeController);
        }else{
            imageView.setImageURI(mImageUrl);
        }
        return imageView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


}

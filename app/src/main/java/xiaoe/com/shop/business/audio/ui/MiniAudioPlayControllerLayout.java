package xiaoe.com.shop.business.audio.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import xiaoe.com.shop.R;
import xiaoe.com.shop.widget.TasksCompletedView;

public class MiniAudioPlayControllerLayout extends FrameLayout {
    private static final String TAG = "MiniAudioPlayController";
    private Context mContext;
    private View rootView;

    public MiniAudioPlayControllerLayout(@NonNull Context context) {
        this(context,null);
    }

    public MiniAudioPlayControllerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(mContext);
    }

    private void initView(Context context) {
        rootView = View.inflate(context, R.layout.layout_mini_audio_play_controller, this);
        TasksCompletedView audioPlayProgress = (TasksCompletedView) rootView.findViewById(R.id.id_audio_play_progress);
        audioPlayProgress.setMaxProgress(100);
        audioPlayProgress.setProgress(80);
    }
}

package xiaoe.com.shop.interfaces;

import android.view.View;

import xiaoe.com.shop.widget.SlidingButtonView;

public interface IonSlidingButtonListener {
    void onMenuIsOpen(View view);
    void onDownOrMove(SlidingButtonView slidingButtonView);
}

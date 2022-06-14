package com.erengulbahar.hichat.swhelper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class SwipeHelper extends ItemTouchHelper.SimpleCallback
{
    int buttonWidth;
    private RecyclerView recyclerView;
    private List<MyButton> buttonList;
    private GestureDetector gestureDetector;
    private int swipePosition = -1;
    private float swipeThreshold = 0.5f;
    private Map<Integer,List<MyButton>> buttonBuffer;
    private Queue<Integer> removerQueue;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        @Override
        public boolean onSingleTapUp(MotionEvent event)
        {
            for(MyButton button : buttonList)
            {
                if(button.onClick(event.getX(),event.getY()))
                {
                    break;
                }
            }

            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            if(swipePosition < 0)
            {
                return false;
            }

            Point point = new Point((int) motionEvent.getRawX(),(int) motionEvent.getRawY());
            RecyclerView.ViewHolder swipeViewHolder = recyclerView.findViewHolderForAdapterPosition(swipePosition);
            View swipedItem = swipeViewHolder.itemView;
            Rect rect = new Rect();
            swipedItem.getGlobalVisibleRect(rect);

            if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_MOVE)
            {
                if(rect.top < point.y && rect.bottom > point.y)
                {
                    gestureDetector.onTouchEvent(motionEvent);
                }

                else
                {
                    removerQueue.add(swipePosition);
                    swipePosition = -1;

                    recoverSwipedItem();
                }
            }

            return false;
        }
    };

    private synchronized void recoverSwipedItem()
    {
        while(!removerQueue.isEmpty())
        {
            int pos = removerQueue.poll();

            if(pos > -1)
            {
                recyclerView.getAdapter().notifyItemChanged(pos);
            }
        }
    }

    public SwipeHelper(Context context, RecyclerView recyclerView, int buttonWidth)
    {
        super(0, ItemTouchHelper.LEFT);
        this.recyclerView = recyclerView;
        this.buttonList = new ArrayList<>();
        this.gestureDetector = new GestureDetector(context, gestureListener);
        this.recyclerView.setOnTouchListener(onTouchListener);
        this.buttonBuffer = new HashMap<>();
        this.buttonWidth = buttonWidth;

        removerQueue = new LinkedList<Integer>()
        {
            @Override
            public boolean add(Integer integer)
            {
                if(contains(integer))
                {
                    return false;
                }

                else
                {
                    return super.add(integer);
                }
            }
        };

        attachSwipe();
    }

    private void attachSwipe()
    {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
    {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
    {
        int pos = viewHolder.getAdapterPosition();

        if(swipePosition != pos)
        {
            removerQueue.add(swipePosition);
        }

        swipePosition = pos;

        if(buttonBuffer.containsKey(swipePosition))
        {
            buttonList = buttonBuffer.get(swipePosition);
        }

        else
        {
            buttonList.clear();
        }

        buttonBuffer.clear();
        swipeThreshold = 0.5f * buttonList.size() * buttonWidth;

        recoverSwipedItem();
    }

    public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder)
    {
        return swipeThreshold;
    }

    @Override
    public float getSwipeEscapeVelocity(float defaultValue)
    {
        return 0.1f * defaultValue;
    }

    @Override
    public float getSwipeVelocityThreshold(float defaultValue)
    {
        return 5.0f * defaultValue;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
    {
        int pos = viewHolder.getAdapterPosition();
        float translationX = dX;
        View itemView = viewHolder.itemView;

        if(pos < 0)
        {
            swipePosition = pos;
            return;
        }

        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
        {
            if(dX < 0)
            {
                List<MyButton> buffer = new ArrayList<>();

                if(!buttonBuffer.containsKey(pos))
                {
                    instantiateMyButton(viewHolder,buffer);
                    buttonBuffer.put(pos,buffer);
                }

                else
                {
                    buffer = buttonBuffer.get(pos);
                }

                translationX = dX * buffer.size() * buttonWidth / itemView.getWidth();
                drawButton(c,itemView,buffer,pos,translationX);
            }
        }

        super.onChildDraw(c,recyclerView,viewHolder,translationX,dY,actionState,isCurrentlyActive);
    }

    private void drawButton(Canvas c, View itemView, List<MyButton> buffer, int pos, float translationX)
    {
        float right = itemView.getRight();
        float dButtonWidth = -1 * translationX / buffer.size();

        for(MyButton button : buffer)
        {
            float left = right - dButtonWidth;
            button.onDraw(c,new RectF(left,itemView.getTop(),right,itemView.getBottom()),pos);
            right = left;
        }
    }

    public abstract void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer);

    public class MyButton
    {
        private int imageResId;
        private int color;
        private int pos;
        private RectF clickRegion;
        private MyButtonClickListener listener;
        private Context context;
        private Resources resources;

        // int pos, RectF clickRegion

        public MyButton(Context context, int imageResId, int color, MyButtonClickListener listener)
        {
            this.imageResId = imageResId;
            this.color = color;
            this.listener = listener;
            this.context = context;

            resources = context.getResources();
        }

        public boolean onClick(float x, float y)
        {
            if(clickRegion != null && clickRegion.contains(x,y))
            {
                listener.onClick(pos);

                return true;
            }

            return false;
        }

        public void onDraw(Canvas canvas, RectF rectF, int pos)
        {
            Paint paint = new Paint();

            paint.setColor(color);
            canvas.drawRect(rectF,paint);

            Rect rect = new Rect();

            float cHeight = rectF.height();
            float cWidth = rectF.width();
            float x = 0;
            float y = 0;

            if(imageResId != 0)
            {
                Drawable drawable = ContextCompat.getDrawable(context,imageResId);
                Bitmap bitmap = drawableToBitmap(drawable);
                canvas.drawBitmap(bitmap,(rectF.right + rectF.left)/2.1f,(rectF.top + rectF.bottom)/2.8f,paint);
            }

            clickRegion = rectF;
            this.pos = pos;
        }

        private Bitmap drawableToBitmap(Drawable drawable)
        {
            if(drawable instanceof BitmapDrawable)
            {
                return ((BitmapDrawable) drawable).getBitmap();
            }

            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        }
    }
}
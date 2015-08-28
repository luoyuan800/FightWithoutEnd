package cn.gavin.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.boredream.fightwithoutend.controller.FightDataInfoController;
import com.boredream.fightwithoutend.domain.Hero;

import java.util.List;

import cn.gavin.Achievement;
import cn.gavin.Maze;
import cn.gavin.R;

public class MainGameActivity extends Activity implements OnClickListener, OnItemClickListener {
    private static final String TAG = "MainGameActivity";

    // 战斗刷新速度
    private long refreshInfoSpeed = 500;

    // 战斗信息
    private ScrollView mainInfoSv;
    private LinearLayout mainInfoPlatform;
    private int fightInfoTotalCount = 50;
    private int fightInfoSize = 20;

    // 英雄
    private Hero hero;
    private cn.gavin.Hero heroN;
    private Maze maze;

    // 人物信息栏
    private TextView itembarContri; // 按钮-属性

    private TextView mainContriHp;
    private TextView mainContriAtt;
    private TextView mainContriDef;
    private TextView swordLev;
    private TextView armorLev;
    private TextView mainContriNdExp;
    private TextView mainContriCurMaterial;
    private TextView clickCount;
    //按钮
    private Button addstr;
    private Button addpow;
    private Button addagi;
    private Button upSword;
    private Button upArmor;
    private Button heroPic;
    private Button pauseButton;
    private Button achievementButton;

    private boolean pause;
    private AchievementAdapter adapter;
    private boolean gameThreadRunning;
    // 是否正在进行一轮战斗,是 正在进行;否 战斗已经结束
    private boolean isOneTurnFinghting = false;

    private GameThread gameThread;

    private int oneTurnIndex = 0;
    private List<String> message;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            refresh();
            switch (msg.what) {
                case 1:
                    if(pause){
                        pause = false;
                        pauseButton.setText("暂停");
                    }else{
                        pause = true;
                        pauseButton.setText("继续");
                    }
                case 4:
                    clickCount.setText("点击\n" + heroN.getClick());
                    heroPic.setBackgroundResource(R.drawable.h_1);
                    break;
                case 5:
                    clickCount.setText("点击\n" + heroN.getClick());
                    heroPic.setBackgroundResource(R.drawable.h_2);
                    break;
                case 10:
                    if (!isOneTurnFinghting) {
                        //monster = getMonster();

                        TextView metMonInfo = new TextView(MainGameActivity.this);
                        metMonInfo.setTextSize(fightInfoSize);
                        mainInfoPlatform.addView(metMonInfo);
                        message = maze.move();
                        if (message.size() <= 5) {
                            heroPic.setBackgroundResource(R.drawable.h_3);
                        } else {
                            heroPic.setBackgroundResource(R.drawable.h_1);
                        }
//                        runOneTurn = FightDataInfoController.runOneTurn(monster);

                        isOneTurnFinghting = true;

                    } else {
                        // 如果一轮战斗信息没有显示完(一轮战斗尚未结束)
                        if (oneTurnIndex < message.size()) {
                            TextView oneKickInfo = new TextView(MainGameActivity.this);
                            // 获取本轮战斗的一次击打信息
//                            FightOneKickData fightOneKickData = runOneTurn.oneKickData
//                                    .get(oneTurnIndex);
                            // 将一次击打信息数据显示到页面中
                            oneKickInfo.setText(message.get(oneTurnIndex));
                            mainInfoPlatform.addView(oneKickInfo);
                            /*if (FightOneKickData.M2H == fightOneKickData.getHarmType()) {
                                mainContriHp.setText(fightOneKickData.getHeroCurrentHp() + "");
                            }*/
                            // 遍历到下一次击打
                            oneTurnIndex++;
                        } else {
                            // 一轮战斗结束了

                            TextView fightEndSeparatorInfo = new TextView(MainGameActivity.this);
                            fightEndSeparatorInfo.setText("--------------------");
                            mainInfoPlatform.addView(fightEndSeparatorInfo);
                            oneTurnIndex = 0;
                            isOneTurnFinghting = false;
                        }
                    }
                    if (mainInfoPlatform.getChildCount() > fightInfoTotalCount) {
                        mainInfoPlatform.removeViewAt(0);
                    }

                    // mainInfoSv.fullScroll(ScrollView.FOCUS_DOWN);
                    scrollToBottom(mainInfoSv, mainInfoPlatform);
                    break;
            }

            super.handleMessage(msg);
        }

    };

    /**
     * SrcollView战斗信息栏滚到最底部
     *
     * @param scroll
     * @param inner
     */
    public void scrollToBottom(final View scroll, final View inner) {

        Handler mHandler = new Handler();

        mHandler.post(new Runnable() {
            public void run() {
                if (scroll == null || inner == null) {
                    return;
                }

                int offset = inner.getMeasuredHeight() - scroll.getHeight();
                if (offset < 0) {
                    offset = 0;
                }

                scroll.scrollTo(0, offset);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_gameview);

        Log.i(TAG, "start game~");

        initGameData();

        gameThreadRunning = true;
        gameThread = new GameThread();
        gameThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameThreadRunning = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                showExitDialog();
                return true;

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 弹出退出程序提示框
     */
    private void showExitDialog() {
        AlertDialog dialog = new Builder(this).create();
        dialog.setTitle("是否退出游戏");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainGameActivity.this.finish();
                        System.exit(0);
                    }

                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        dialog.show();
    }

    private TextView achievementDesc;

    private void showAchievement() {
        AlertDialog dialog = new Builder(this).create();
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ListView listView = new ListView(this);
        adapter = new AchievementAdapter();
        listView.setAdapter(adapter);
        listView.setForegroundGravity(Gravity.FILL_HORIZONTAL);
        linearLayout.addView(listView);
        achievementDesc = new TextView(this);
        linearLayout.addView(achievementDesc);
        dialog.setView(linearLayout);
        dialog.setTitle("成就");
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "退出", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initGameData() {
        // 英雄
        hero = FightDataInfoController.hero;
        heroN = new cn.gavin.Hero("ly");
        maze = new Maze(heroN);
        // 左侧战斗信息
        mainInfoSv = (ScrollView) findViewById(R.id.main_info_sv);
        mainInfoPlatform = (LinearLayout) findViewById(R.id.main_info_ll);
        // ---- ---- 标题（人物名称 | 最深迷宫数)
        itembarContri = (TextView) findViewById(R.id.character_itembar_contribute);
        // ---- ---- 属性
        mainContriHp = (TextView) findViewById(R.id.main_contri_hp);
        mainContriAtt = (TextView) findViewById(R.id.main_contri_att);
        mainContriDef = (TextView) findViewById(R.id.main_contri_def);
        swordLev = (TextView) findViewById(R.id.main_contri_level);
        armorLev = (TextView) findViewById(R.id.main_armor_level);
        mainContriNdExp = (TextView) findViewById(R.id.main_contri_needexp);
        mainContriCurMaterial = (TextView) findViewById(R.id.main_contri_currentexp);
        addstr = (Button) findViewById(R.id.main_contri_add_str);
        addstr.setOnClickListener(this);
        addagi = (Button) findViewById(R.id.main_contri_add_agi);
        addagi.setOnClickListener(this);
        addpow = (Button) findViewById(R.id.main_contri_add_pow);
        addpow.setOnClickListener(this);
        upSword = (Button) findViewById(R.id.up_sword);
        upSword.setOnClickListener(this);
        upArmor = (Button) findViewById(R.id.up_armor);
        upArmor.setOnClickListener(this);
        heroPic = (Button) findViewById(R.id.hero_pic);
        heroPic.setOnClickListener(this);
        heroPic.setBackgroundResource(R.drawable.h_1);
        heroPic.setTextColor(getResources().getColor(R.color.red));
        heroPic.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        pauseButton = (Button) findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(this);
        achievementButton = (Button)findViewById(R.id.achieve_button);
        achievementButton.setOnClickListener(this);
        clickCount = (TextView) findViewById(R.id.hero_pic_click_count);
        clickCount.setText("点击\n" + heroN.getClick());
        refresh();
    }

    private void refresh() {
        mainContriHp.setText(heroN.getHp() + "");
        mainContriAtt.setText(heroN.getAttackValue() + "");
        mainContriDef.setText(heroN.getDefenseValue() + "");
        swordLev.setText(heroN.getSword() + "\n+" + heroN.getSwordLev());
        armorLev.setText(heroN.getArmor() + "\n+" + heroN.getArmorLev());
        mainContriCurMaterial.setText(heroN.getMaterial() + "");
        mainContriNdExp.setText(heroN.getPoint() + "");
        if (heroN.getMaterial() >= 100 + heroN.getSwordLev()) {
            upSword.setEnabled(true);
        } else {
            upSword.setEnabled(false);
        }
        if (heroN.getMaterial() >= 80 + heroN.getArmorLev()) {
            upArmor.setEnabled(true);
        } else {
            upArmor.setEnabled(false);
        }
        if (heroN.getPoint() > 0) {
            addpow.setEnabled(true);
            addstr.setEnabled(true);
            addagi.setEnabled(true);
        } else {
            addpow.setEnabled(false);
            addstr.setEnabled(false);
            addagi.setEnabled(false);
        }
        itembarContri.setText(heroN.getName() + "\n迷宫到达(当前/记录） " + maze.getLev() + "/" + heroN.getMaxMazeLev() + "层");
    }

    private class GameThread extends Thread {

        @Override
        public void run() {
            while (gameThreadRunning) {
                try {
                    Thread.sleep(refreshInfoSpeed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!pause) {
                    handler.sendEmptyMessage(10);
                }

            }
        }
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick() -- " + v.getId() + " -- 被点击了");
        switch (v.getId()) {
            case R.id.pause_button:
                handler.sendEmptyMessage(1);
                break;
            case R.id.achieve_button:
                showAchievement();
                break;
            case R.id.up_armor:
                heroN.upgradeArmor();
                break;
            case R.id.up_sword:
                heroN.upgradeSword();
                break;
            case R.id.main_contri_add_agi:
                heroN.addAgility();
                break;
            case R.id.main_contri_add_pow:
                heroN.addLife();
                break;
            case R.id.main_contri_add_str:
                heroN.addStrength();
                break;
            case R.id.hero_pic:
                heroN.click();
                if (heroN.getClick() % 2 == 0) {
                    handler.sendEmptyMessage(4);
                } else {
                    handler.sendEmptyMessage(5);
                }
                break;
            default:
                break;
        }
    }

    class AchievementAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Achievement.values().length;
        }

        @Override
        public Achievement getItem(int position) {
            if (position >= getCount()) position = 0;
            return Achievement.values()[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            AchievementViewHolder holder;
            if (convertView == null) {
                holder = new AchievementViewHolder();
                convertView = View.inflate(MainGameActivity.this,
                        R.layout.achievement_list_item, null);
                holder.name = (Button) convertView.findViewById(R.id.achieve_name);
                holder.name.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        achievementDesc.setText(getItem(position).getDesc());
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (AchievementViewHolder) convertView.getTag();
            }
            Achievement item = getItem(position);
            holder.name.setText(item.getName());
            if (!item.isEnable()) {
                holder.name.setEnabled(false);
            } else {
                holder.name.setEnabled(true);
            }
            return convertView;
        }

    }

    static class AchievementViewHolder {
        Button name;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}

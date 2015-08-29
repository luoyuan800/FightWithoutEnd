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
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import cn.gavin.*;

import java.io.*;
import java.util.List;

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
    private cn.gavin.Hero heroN;
    private Maze maze;

    // 人物信息栏
    private TextView itembarContri; // 按钮-属性

    private TextView mainContriHp;
    private TextView mainContriAtt;
    private TextView mainContriDef;
    private TextView swordLev;
    private TextView armorLev;
    private TextView heroPointValue;
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
                    if (pause) {
                        pause = false;
                        pauseButton.setText("暂停");
                    } else {
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
                        if (message.size() <= 4) {
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
                        save();
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
        if (!load()) {
            heroN = new cn.gavin.Hero("ly");
            maze = new Maze(heroN);
        }
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
        heroPointValue = (TextView) findViewById(R.id.main_contri_needexp);
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
        achievementButton = (Button) findViewById(R.id.achieve_button);
        achievementButton.setOnClickListener(this);
        clickCount = (TextView) findViewById(R.id.hero_pic_click_count);
        clickCount.setText("点击\n" + heroN.getClick());
        refresh();
    }

    private void refresh() {
        mainContriHp.setText(heroN.getHp() + "");
        mainContriAtt.setText(heroN.getUpperAtk() + "");
        mainContriDef.setText(heroN.getUpperDef() + "");
        swordLev.setText(heroN.getSword() + "\n+" + heroN.getSwordLev());
        armorLev.setText(heroN.getArmor() + "\n+" + heroN.getArmorLev());
        mainContriCurMaterial.setText(heroN.getMaterial() + "");
        heroPointValue.setText(heroN.getPoint() + "");
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
        itembarContri.setText(heroN.getName() + "\n迷宫到达(当前/记录）层\n" + maze.getLev() + "/" + heroN.getMaxMazeLev());
    }

    private long saveTime = 0;

    private class GameThread extends Thread {

        @Override
        public void run() {
            while (gameThreadRunning) {
                try {
                    Thread.sleep(refreshInfoSpeed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!pause) {
                    saveTime += refreshInfoSpeed;
                    handler.sendEmptyMessage(10);
                    if (saveTime >= refreshInfoSpeed * 60)
                        save();
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
                handler.sendEmptyMessage(0);
                break;
            case R.id.up_sword:
                heroN.upgradeSword();
                handler.sendEmptyMessage(0);
                break;
            case R.id.main_contri_add_agi:
                heroN.addAgility();
                handler.sendEmptyMessage(0);
                break;
            case R.id.main_contri_add_pow:
                heroN.addLife();
                handler.sendEmptyMessage(0);
                break;
            case R.id.main_contri_add_str:
                heroN.addStrength();
                handler.sendEmptyMessage(0);
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

    public static class AchievementList {
        Achievement a0;
        Achievement a1;
        Achievement a2;

        public AchievementList(Achievement a0, Achievement a1, Achievement a2) {
            this.a0 = a0;
            this.a1 = a1;
            this.a2 = a2;
        }
    }

    private final List<AchievementList> adapterData = Achievement.getAchievementListAdp();

    class AchievementAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return adapterData.size();
        }

        @Override
        public AchievementList getItem(int position) {
            if (position >= getCount()) position = 0;
            return adapterData.get(position);
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
                        achievementDesc.setText(getItem(position).a0.getDesc());
                    }
                });
                holder.name1 = (Button) convertView.findViewById(R.id.achieve_name_1);
                holder.name1.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        achievementDesc.setText(getItem(position).a1.getDesc());
                    }
                });
                holder.name2 = (Button) convertView.findViewById(R.id.achieve_name_2);
                holder.name2.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        achievementDesc.setText(getItem(position).a2.getDesc());
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (AchievementViewHolder) convertView.getTag();
            }
            AchievementList item = getItem(position);
            holder.name.setText(item.a0.getName());
            if (!item.a0.isEnable()) {
                holder.name.setEnabled(false);
            } else {
                holder.name.setEnabled(true);
            }
            holder.name1.setText(item.a1.getName());
            if (!item.a1.isEnable()) {
                holder.name1.setEnabled(false);
            } else {
                holder.name1.setEnabled(true);
            }
            holder.name2.setText(item.a2.getName());
            if (!item.a2.isEnable()) {
                holder.name2.setEnabled(false);
            } else {
                holder.name2.setEnabled(true);
            }
            return convertView;
        }

    }

    static class AchievementViewHolder {
        Button name;
        Button name1;
        Button name2;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void save() {
        try {
            FileOutputStream fos = this.openFileOutput("yzcmg.ave", Activity.MODE_PRIVATE);
            StringBuffer sb = new StringBuffer();
            sb.append(heroN.getName()).append("_").append(heroN.getHp()).append("_").append(heroN.getUpperHp()).
                    append("_").append(heroN.getBaseAttackValue()).append("_").append(heroN.getBaseDefense()).append("_").
                    append(heroN.getClick()).append("_").append(heroN.getPoint()).append("_").append(heroN.getMaterial())
                    .append("_").append(heroN.getSwordLev()).append("_").append(heroN.getArmorLev()).append("_").
                    append(heroN.getSword()).append("_").append(heroN.getArmor()).append("_").append(heroN.getMaxMazeLev())
                    .append("_").append(heroN.getStrength()).append("_").append(heroN.getPower()).append("_").
                    append(heroN.getAgility()).append("_").append(heroN.getClickAward());
            sb.append("_");
            for (Achievement achievement : Achievement.values()) {
                if (achievement.isEnable()) {
                    sb.append(1);
                } else {
                    sb.append(0);
                }
            }
            sb.append("_");
            sb.append(maze.getLev());
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean load() {
        try {
            FileInputStream fis = openFileInput("yzcmg.ave");
            byte[] b = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (fis.read(b) != -1) {
                baos.write(b, 0, b.length);
            }
            baos.close();
            fis.close();
            String save = baos.toString();
            String[] atts = save.split("_");
            if (atts.length == 19) {
                heroN = new Hero(atts[0]);
                heroN.setHp(Integer.parseInt(atts[1]));
                heroN.setUpperHp(Integer.parseInt(atts[2]));
                heroN.setAttackValue(Integer.parseInt(atts[3]));
                heroN.setDefenseValue(Integer.parseInt(atts[4]));
                heroN.setClick(Integer.parseInt(atts[5]));
                heroN.setPoint(Integer.parseInt(atts[6]));
                heroN.setMaterial(Integer.parseInt(atts[7]));
                heroN.setSwordLev(Integer.parseInt(atts[8]));
                heroN.setArmorLev(Integer.parseInt(atts[9]));
                heroN.setMaxMazeLev(Integer.parseInt(atts[12]));
                heroN.setStrength(Integer.parseInt(atts[13]));
                heroN.setPower(Integer.parseInt(atts[14]));
                heroN.setAgility(Integer.parseInt(atts[15]));
                heroN.setClickAward(Integer.parseInt(atts[16]));
                heroN.setSword(Sword.valueOf(atts[10]));
                heroN.setArmor(Armor.valueOf(atts[11]));
                maze = new Maze(heroN);
                maze.setLevel(Integer.parseInt(atts[18]));
                for (int i = 0; i < atts[17].length() && i < Achievement.values().length; i++) {
                    int enable = (int) (atts[17].charAt(i));
                    if (enable == 1) {
                        Achievement.values()[i].enable();
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

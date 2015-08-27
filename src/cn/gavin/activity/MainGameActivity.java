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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.boredream.fightwithoutend.controller.FightDataInfoController;
import com.boredream.fightwithoutend.domain.Hero;
import com.boredream.fightwithoutend.domain.Monster;
import com.boredream.fightwithoutend.domain.Skill;
import com.boredream.fightwithoutend.domain.Treasure;

import java.util.ArrayList;
import java.util.List;

import cn.gavin.Maze;
import cn.gavin.R;

public class MainGameActivity extends Activity implements OnClickListener, OnItemClickListener {
    private static final String TAG = "MainGameActivity";

    // ս��ˢ���ٶ�
    private long refreshInfoSpeed = 500;

    // ս����Ϣ
    private ScrollView mainInfoSv;
    private LinearLayout mainInfoPlatform;
    private int fightInfoTotalCount = 50;
    private int fightInfoSize = 20;

    // Ӣ��
    private Hero hero;
    private cn.gavin.Hero heroN;
    private Maze maze;
    // ����Ϣ��
    private TextView rootItemBarCharacter; // ��ť-����
    private TextView rootItemBarOther; // ��ť-����

    // ������Ϣ��
    private LinearLayout rootItemCharacter;

    private TextView itembarContri; // ��ť-����
    private TextView itembarEquip; // ��ť-װ��
    private TextView itembarGoods; // ��ť-��Ʒ

    private LinearLayout itemContri; // ��������-����
    private TextView mainContriHp;
    private TextView mainContriAtt;
    private TextView mainContriDef;
    private TextView swordLev;
    private TextView armorLev;
    private TextView mainContriNdExp;
    private TextView mainContriCurMaterial;
    private TextView mainContriSp;
    //��ť
    private Button addstr;
    private Button addpow;
    private Button addagi;
    private Button upSword;
    private Button upArmor;
    private Button heroPic;

    private LinearLayout itemEquip; // ��������-װ��
    private TextView equipWeapon;
    private TextView equipArmor;

    private LinearLayout itemGoods; // ��������-��Ʒ
    private ListView itemGoodsCountainer;
    private ItemGoodsAdapter itemGoodsAdapter;

    // ������Ϣ��
    private LinearLayout rootItemOther;

    private TextView itembarShop; // ��ť-�̵�
    private TextView itembarMap; // ��ť-��ͼ
    private TextView itembarSkill; // ��ť-����

    private LinearLayout itemShop; // ��������-�̵�

    private LinearLayout itemMap; // ��������-��ͼ

    private LinearLayout itemSkill; // ��������-����
    private ListView itemSkillCountainer;
    private ItemSkillAdapter itemSkillAdapter;

    // ����
    private ArrayList<Monster> monsters;
    private Monster monster;

    private boolean gameThreadRunning;
    // �Ƿ����ڽ���һ��ս��,�� ���ڽ���;�� ս���Ѿ�����
    private boolean isOneTurnFinghting = false;

    private GameThread gameThread;

    private int oneTurnIndex = 0;
    private List<String> message;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            refresh();
            switch (msg.what) {
                case 4:
                    heroPic.setText("���\n" + heroN.getClick());
                    heroPic.setBackgroundResource(R.drawable.h_1);
                    break;
                case 5:
                    heroPic.setText("���\n" + heroN.getClick());
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
                        // ���һ��ս����Ϣû����ʾ��(һ��ս����δ����)
                        if (oneTurnIndex < message.size()) {
                            TextView oneKickInfo = new TextView(MainGameActivity.this);
                            // ��ȡ����ս����һ�λ�����Ϣ
//                            FightOneKickData fightOneKickData = runOneTurn.oneKickData
//                                    .get(oneTurnIndex);
                            // ��һ�λ�����Ϣ������ʾ��ҳ����
                            oneKickInfo.setText(message.get(oneTurnIndex));
                            mainInfoPlatform.addView(oneKickInfo);
                            /*if (FightOneKickData.M2H == fightOneKickData.getHarmType()) {
                                mainContriHp.setText(fightOneKickData.getHeroCurrentHp() + "");
                            }*/
                            // ��������һ�λ���
                            oneTurnIndex++;
                        } else {
                            // һ��ս��������

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
     * SrcollViewս����Ϣ��������ײ�
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
     * �����˳�������ʾ��
     */
    private void showExitDialog() {
        AlertDialog dialog = new Builder(this).create();
        dialog.setTitle("�Ƿ��˳���Ϸ");
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ��",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainGameActivity.this.finish();
                        System.exit(0);
                    }

                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "ȡ��",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });
        dialog.show();
    }

    private void initGameData() {
        // Ӣ��
        hero = FightDataInfoController.hero;
        heroN = new cn.gavin.Hero("123");
        maze = new Maze(heroN);
        // ���ս����Ϣ
        mainInfoSv = (ScrollView) findViewById(R.id.main_info_sv);
        mainInfoPlatform = (LinearLayout) findViewById(R.id.main_info_ll);
        // ---- ������Ϣ��
        /*itembarEquip = (TextView) findViewById(R.id.character_itembar_equip);
        itembarGoods = (TextView) findViewById(R.id.character_itembar_goods);*/
        // ---- ---- ����
        itemContri = (LinearLayout) findViewById(R.id.character_item_contribute);
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
        heroPic.setText("���\n" + heroN.getClick());
        heroPic.setTextColor(getResources().getColor(R.color.red, null));
        heroPic.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        refresh();
    }

    private void refresh() {
        mainContriHp.setText(heroN.getHp() + "");
        mainContriAtt.setText(heroN.getAttackValue() + "");
        mainContriDef.setText(heroN.getDefenseValue() + "");
        swordLev.setText(heroN.getSwordLev() + "");
        armorLev.setText(heroN.getArmorLev() + "");
        mainContriCurMaterial.setText(heroN.getMaterial() + "");
        mainContriNdExp.setText(heroN.getPoint() + "");
        if (heroN.getMaterial() >= 10 + heroN.getSwordLev()) {
            upSword.setEnabled(true);
        } else {
            upSword.setEnabled(false);
        }
        if (heroN.getMaterial() >= 10 + heroN.getArmorLev()) {
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

                handler.sendEmptyMessage(10);

            }
        }
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick() -- " + v.getId() + " -- �������");
        switch (v.getId()) {
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

    class ItemSkillAdapter extends BaseAdapter {

        private List<Skill> skills;

        public ItemSkillAdapter() {
            skills = hero.getExistSkill();
        }

        @Override
        public int getCount() {
            return skills.size();
        }

        @Override
        public Skill getItem(int position) {
            return skills.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SkillViewHolder holder;
            if (convertView == null) {
                holder = new SkillViewHolder();
                convertView = View.inflate(MainGameActivity.this,
                        R.layout.skill_item, null);
                holder.skillName = (TextView) convertView.findViewById(R.id.skill_item_name);
                holder.skillLevel = (TextView) convertView.findViewById(R.id.skill_item_level);
                holder.skillRise = (ImageButton) convertView.findViewById(R.id.skill_item_rise);
                convertView.setTag(holder);
            } else {
                holder = (SkillViewHolder) convertView.getTag();
            }
            final Skill skill = getItem(position);
            holder.skillName.setText(skill.getName());
            holder.skillLevel.setText(skill.getLevel() + "");
            holder.skillRise.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // hero.riseSkill(skill);
                    FightDataInfoController.riseHeroSkill(skill);
                    mainContriSp.setText(hero.sp + "");
                    Log.i(TAG, "the skill " + skill.getName() + "'lv is up : "
                            + (skill.getLevel() - 1) + "->" + skill.getLevel());
                    itemSkillAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }
    }

    static class SkillViewHolder {
        TextView skillName;
        TextView skillLevel;
        ImageButton skillRise;
    }

    class ItemGoodsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Hero.MAX_GOODS_COUNT;
        }

        @Override
        public Treasure getItem(int position) {
            return hero.totalObtainTreasure.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TreasureViewHolder holder;
            if (convertView == null) {
                holder = new TreasureViewHolder();
                convertView = View.inflate(MainGameActivity.this,
                        R.layout.treasure_countainer_item, null);
                holder.treasureName = (TextView) convertView.findViewById(R.id.treasure_item_name);
                convertView.setTag(holder);
            } else {
                holder = (TreasureViewHolder) convertView.getTag();
            }
            holder.treasureName.setText(getItem(position).getName());
            return convertView;
        }

    }

    static class TreasureViewHolder {
        TextView treasureName;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // ��Ʒitem���
        if (parent == itemGoodsCountainer) {
            Log.i(TAG, "onItemClick() -- position=" + position +
                    ";obj=" + itemGoodsAdapter.getItem(position));
            Treasure treasure = itemGoodsAdapter.getItem(position);
            FightDataInfoController.equip(treasure);
            if (hero.currentWeapon != null) {
                equipWeapon.setText(hero.currentWeapon.getName());
            }
            if (hero.currentArmor != null) {
                equipArmor.setText(hero.currentArmor.getName());
            }
            mainContriAtt.setText(hero.getAttackValue() + "");
            mainContriDef.setText(hero.getDefenseValue() + "");
            itemGoodsAdapter.notifyDataSetChanged();
        }
        // ����item���
        else if (parent == itemSkillCountainer) {
            Log.i(TAG, "onItemClick() -- position=" + position +
                    ";obj=" + itemSkillAdapter.getItem(position));
        }
    }

}

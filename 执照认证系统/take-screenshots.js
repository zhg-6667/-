const { chromium } = require('playwright');
const path = require('path');

const FILE_URL = 'file:///' + path.resolve(__dirname, 'prototype-h5.html').replace(/\\/g, '/');
const OUT = path.resolve(__dirname, 'screenshots');

async function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }

async function shot(page, name, frameOnly = true) {
  await sleep(400);
  if (frameOnly) {
    const frame = await page.$('.phone-frame');
    if (frame) {
      await frame.screenshot({ path: path.join(OUT, name + '.png') });
      console.log('  ✓ ' + name);
      return;
    }
  }
  await page.screenshot({ path: path.join(OUT, name + '.png') });
  console.log('  ✓ ' + name);
}

async function clickTab(page, idx) {
  const tabs = await page.$$('.tab-item');
  if (tabs[idx]) await tabs[idx].click();
  await sleep(500);
}

(async () => {
  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext({ viewport: { width: 500, height: 850 } });
  const page = await context.newPage();

  // Clear localStorage for clean state
  await page.goto(FILE_URL);
  await page.evaluate(() => {
    const keys = Object.keys(localStorage).filter(k => k.startsWith('lc_'));
    keys.forEach(k => localStorage.removeItem(k));
  });
  await page.reload();
  await sleep(1000);

  // ========== 1. 练习首页 ==========
  console.log('1. 练习首页');
  await shot(page, '01-练习首页');

  // ========== 2. 题库练习页 ==========
  console.log('2. 题库练习页');
  // Click "开始做题" button
  const startBtn = await page.$('text=开始做题');
  if (startBtn) await startBtn.click();
  await sleep(600);
  await shot(page, '02-题库练习页');

  // ========== 3. 练习答题页 - 未作答 ==========
  console.log('3. 练习答题页');
  const allPractice = await page.$('text=全部练习');
  if (allPractice) await allPractice.click();
  await sleep(600);
  await shot(page, '03-练习答题页-未作答');

  // ========== 4. 练习答题页 - 回答正确 ==========
  console.log('4. 回答正确');
  // Try clicking the correct answer option (first option for first question usually index 0)
  await page.evaluate(() => {
    // Find quiz and select correct answer
    if (typeof selectQuizOpt === 'function') {
      const q = window.quiz || window.QBANK;
      // Try answer index from question data
      selectQuizOpt(0);
    }
  });
  await sleep(300);
  // Click first option
  const opts = await page.$$('.phone-inner .page.active [onclick*="selectQuizOpt"]');
  if (opts.length > 0) await opts[0].click();
  await sleep(800);
  await shot(page, '04-练习答题页-回答后');

  // ========== 5. 答题卡面板 ==========
  console.log('5. 答题卡面板');
  // Click "答题卡" button
  const aqBtn = await page.$('.phone-inner .page.active [onclick*="toggleQPanel"], .phone-inner .page.active [onclick*="Panel"]');
  if (aqBtn) await aqBtn.click();
  await sleep(500);
  await shot(page, '05-答题卡面板');
  // Close panel
  const aqMask = await page.$('.aq-mask[style*="block"], .aq-mask.show');
  if (aqMask) await aqMask.click();
  await sleep(300);

  // ========== 6. Go back, do mock exam ==========
  console.log('6. 模拟考试');
  // Navigate back to home
  await page.evaluate(() => {
    if (typeof switchTab === 'function') switchTab('pg-practice');
  });
  await sleep(500);
  // Start mock exam
  await page.evaluate(() => {
    if (typeof startMock === 'function') startMock();
  });
  await sleep(600);
  await shot(page, '06-模拟考试答题页');

  // Answer some questions then submit for result
  console.log('7. 模拟考试-答题+交卷');
  for (let i = 0; i < 3; i++) {
    await page.evaluate((idx) => {
      if (typeof selectExamOpt === 'function') selectExamOpt(0);
      setTimeout(() => { if (typeof examNext === 'function') examNext(); }, 200);
    }, i);
    await sleep(500);
  }
  // Submit exam
  await page.evaluate(() => {
    if (typeof submitExam === 'function') submitExam();
  });
  await sleep(800);
  await shot(page, '07-练习结果页');

  // ========== 8. 登录页 ==========
  console.log('8. 登录页');
  await page.evaluate(() => {
    if (typeof switchTab === 'function') switchTab('pg-skills');
  });
  await sleep(600);
  await shot(page, '08-登录页');

  // ========== 9. 登录 - 输入手机号 ==========
  console.log('9. 登录流程');
  const phoneInput = await page.$('#loginPhone');
  if (phoneInput) {
    await phoneInput.fill('13800001111');
  }
  await sleep(300);
  const smsInput = await page.$('#loginSms');
  if (smsInput) {
    await smsInput.fill('1234');
  }
  // Check agreement
  await page.evaluate(() => {
    if (typeof toggleAgree === 'function') {
      const box = document.getElementById('agreeBox');
      if (box && !box.classList.contains('checked')) toggleAgree();
    }
  });
  await sleep(300);
  await shot(page, '09-登录页-已填写');

  // Do login
  await page.evaluate(() => {
    if (typeof doLogin === 'function') doLogin();
  });
  await sleep(1000);

  // ========== 10. 技能选择页 ==========
  console.log('10. 技能选择页');
  await page.evaluate(() => {
    if (typeof switchTab === 'function') switchTab('pg-skills');
  });
  await sleep(600);
  await shot(page, '10-技能选择页');

  // ========== 11. 科目列表页 ==========
  console.log('11. 科目列表页');
  await page.evaluate(() => {
    if (typeof clickSkill === 'function') clickSkill(0);
  });
  await sleep(600);
  await shot(page, '11-科目列表页');

  // ========== 12. 正式考试答题页 ==========
  console.log('12. 正式考试');
  await page.evaluate(() => {
    if (typeof startExam === 'function') startExam(0, 2); // 科目三 (待考试)
  });
  await sleep(800);
  await shot(page, '12-正式考试答题页');

  // Answer a question
  await page.evaluate(() => {
    if (typeof selectExamOpt === 'function') selectExamOpt(1);
  });
  await sleep(400);
  await shot(page, '13-正式考试-已选答案');

  // Show exam answer panel
  await page.evaluate(() => {
    if (typeof toggleEPanel === 'function') toggleEPanel();
  });
  await sleep(500);
  await shot(page, '14-正式考试-答题卡');
  await page.evaluate(() => {
    if (typeof toggleEPanel === 'function') toggleEPanel();
  });
  await sleep(300);

  // Answer all questions and submit
  console.log('13. 交卷');
  await page.evaluate(() => {
    if (window.quiz && window.quiz.qs) {
      for (let i = 0; i < window.quiz.qs.length; i++) {
        window.quiz.ans[i] = 0;
      }
    }
    if (typeof submitExam === 'function') submitExam();
  });
  await sleep(1000);

  // ========== 14. 考试结果页 ==========
  console.log('14. 考试结果页');
  await shot(page, '15-考试结果页');

  // ========== 15. 答题详情页 ==========
  console.log('15. 答题详情页');
  await page.evaluate(() => {
    if (typeof viewDetail === 'function') viewDetail(0);
  });
  await sleep(600);
  await shot(page, '16-答题详情页');

  // ========== 16. 执照申领页 ==========
  console.log('16. 执照申领页');
  await page.evaluate(() => {
    if (typeof switchTab === 'function') switchTab('pg-license');
  });
  await sleep(600);
  await shot(page, '17-执照申领页');

  // ========== 17. 个人中心 ==========
  console.log('17. 个人中心');
  await page.evaluate(() => {
    if (typeof switchTab === 'function') switchTab('pg-my');
  });
  await sleep(600);
  await shot(page, '18-个人中心');

  // ========== 18. 账号信息 ==========
  console.log('18. 账号信息');
  await page.evaluate(() => {
    if (typeof navTo === 'function') navTo('pg-account');
  });
  await sleep(600);
  await shot(page, '19-账号信息页');

  // ========== 19. 证件信息 ==========
  console.log('19. 证件信息');
  await page.evaluate(() => {
    if (typeof navTo === 'function') navTo('pg-cert');
  });
  await sleep(600);
  await shot(page, '20-证件信息页');

  // ========== 20. 认证材料 ==========
  console.log('20. 认证材料');
  await page.evaluate(() => {
    if (typeof navTo === 'function') navTo('pg-materials');
  });
  await sleep(600);
  await shot(page, '21-认证材料页');

  // ========== 21. 考试记录 ==========
  console.log('21. 考试记录');
  await page.evaluate(() => {
    if (typeof navTo === 'function') navTo('pg-records');
  });
  await sleep(600);
  await shot(page, '22-考试记录页');

  // ========== 22. 支付页 ==========
  console.log('22. 支付页');
  await page.evaluate(() => {
    if (typeof switchTab === 'function') switchTab('pg-skills');
  });
  await sleep(400);
  await page.evaluate(() => {
    if (typeof clickSkill === 'function') clickSkill(1); // 母婴护理 (未支付)
  });
  await sleep(600);
  await shot(page, '23-支付认证费页');

  // ========== 23. New user onboarding ==========
  console.log('23. 信息完善引导页');
  // Logout first
  await page.evaluate(() => {
    if (typeof doLogout === 'function') {
      // Simulate direct logout
      localStorage.removeItem('lc_currentPhone');
      window.user = null;
      if (typeof switchTab === 'function') switchTab('pg-practice');
    }
  });
  await sleep(400);
  // Login with new number to trigger onboarding
  await page.evaluate(() => {
    if (typeof switchTab === 'function') switchTab('pg-skills');
  });
  await sleep(500);
  const phoneInput2 = await page.$('#loginPhone');
  if (phoneInput2) await phoneInput2.fill('13900009999');
  const smsInput2 = await page.$('#loginSms');
  if (smsInput2) await smsInput2.fill('1234');
  await page.evaluate(() => {
    const box = document.getElementById('agreeBox');
    if (box && !box.classList.contains('checked') && typeof toggleAgree === 'function') toggleAgree();
  });
  await sleep(200);
  await page.evaluate(() => {
    if (typeof doLogin === 'function') doLogin();
  });
  await sleep(1000);
  await shot(page, '24-信息完善-步骤1');

  // Step 2
  await page.evaluate(() => {
    if (typeof obNext === 'function') obNext();
  });
  await sleep(500);
  await shot(page, '25-信息完善-步骤2-身份证');

  // Step 3
  await page.evaluate(() => {
    if (typeof obNext === 'function') obNext();
  });
  await sleep(500);
  await shot(page, '26-信息完善-步骤3-健康证');

  // Step 4
  await page.evaluate(() => {
    if (typeof obNext === 'function') obNext();
  });
  await sleep(500);
  await shot(page, '27-信息完善-步骤4-体检报告');

  // Step 5
  await page.evaluate(() => {
    if (typeof obNext === 'function') obNext();
  });
  await sleep(500);
  await shot(page, '28-信息完善-步骤5-背调验证');

  console.log('\n✅ All screenshots saved to: screenshots/');
  await browser.close();
})();

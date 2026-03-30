const { chromium } = require('playwright');
const path = require('path');

const FILE_URL = 'file:///' + path.resolve(__dirname, 'prototype-h5.html').replace(/\\/g, '/');
const OUT = path.resolve(__dirname, 'screenshots');

async function sleep(ms) { return new Promise(r => setTimeout(r, ms)); }

async function shot(page, name) {
  await sleep(400);
  const frame = await page.$('.phone-frame');
  if (frame) {
    await frame.screenshot({ path: path.join(OUT, name + '.png') });
    console.log('  ✓ ' + name);
  }
}

(async () => {
  const browser = await chromium.launch({ headless: true });
  const context = await browser.newContext({ viewport: { width: 500, height: 850 } });
  const page = await context.newPage();

  await page.goto(FILE_URL);
  // Clear login state for new user flow
  await page.evaluate(() => {
    localStorage.removeItem('lc_currentPhone');
    localStorage.removeItem('lc_inited');
  });
  await page.reload();
  await sleep(1000);

  // Go to skills tab (triggers login page)
  console.log('1. Navigate to login for new user');
  await page.evaluate(() => {
    if (typeof switchTab === 'function') switchTab('pg-skills');
  });
  await sleep(600);

  // Now login page should be visible - fill it
  console.log('2. Fill login form with new phone');
  await page.evaluate(() => {
    const phoneEl = document.getElementById('loginPhone');
    const smsEl = document.getElementById('loginSms');
    if (phoneEl) { phoneEl.value = '13900009999'; phoneEl.dispatchEvent(new Event('input')); }
    if (smsEl) { smsEl.value = '1234'; smsEl.dispatchEvent(new Event('input')); }
    // Check agreement
    const box = document.getElementById('agreeBox');
    if (box && !box.classList.contains('checked') && typeof toggleAgree === 'function') toggleAgree();
  });
  await sleep(300);

  // Login
  console.log('3. Login');
  await page.evaluate(() => {
    if (typeof doLogin === 'function') doLogin();
  });
  await sleep(1500);

  // Should be on onboarding page now
  console.log('4. Onboarding screenshots');
  await shot(page, '24-信息完善-步骤1-账号信息');

  // Click upload areas to simulate uploads, then next
  await page.evaluate(() => {
    // Simulate photo upload
    const uploads = document.querySelectorAll('#pg-onboard .upload-area');
    if (uploads[0]) { uploads[0].click(); }
  });
  await sleep(800);
  await shot(page, '24b-信息完善-步骤1-已上传');

  // Step 2
  await page.evaluate(() => { if (typeof obNext === 'function') obNext(); });
  await sleep(600);
  await shot(page, '25-信息完善-步骤2-身份证');

  // Simulate ID upload
  await page.evaluate(() => {
    const uploads = document.querySelectorAll('#pg-onboard .upload-area');
    uploads.forEach(u => u.click());
  });
  await sleep(800);
  await shot(page, '25b-信息完善-步骤2-已上传');

  // Step 3
  await page.evaluate(() => { if (typeof obNext === 'function') obNext(); });
  await sleep(600);
  await shot(page, '26-信息完善-步骤3-健康证');

  // Step 4
  await page.evaluate(() => { if (typeof obNext === 'function') obNext(); });
  await sleep(600);
  await shot(page, '27-信息完善-步骤4-体检报告');

  // Step 5
  await page.evaluate(() => { if (typeof obNext === 'function') obNext(); });
  await sleep(600);
  await shot(page, '28-信息完善-步骤5-背调验证');

  console.log('\n✅ Onboarding screenshots complete!');
  await browser.close();
})();

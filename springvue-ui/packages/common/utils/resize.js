import { ref } from "vue";

// 不固定宽高比的窗口适配
// npm i postcss-pxtorem autoprefixer amfe-flexible --save-dev

// module.exports = {
//   plugins: {
//     autoprefixer: {
//       overrideBrowserslist: [
//         "Android 4.1",
//         "iOS 7.1",
//         "Chrome > 31",
//         "ff > 31",
//         "ie >= 8",
//         "last 10 versions", // 所有主流浏览器最近10版本用
//       ],
//       grid: true,
//     },
//     "postcss-pxtorem": {
//       rootValue: 192, // 设计稿宽度的1/ 10 例如设计稿按照 1920设计 此处就为192
//       propList: ["*", "!border"], // 除 border 外所有px 转 rem
//       selectorBlackList: [".el-"], // 过滤掉.el-开头的class，不进行rem转换
//     },
//   },
// };

// 适合固定宽高比的窗口适配
export default function windowResize(width, height) {
  // * 指向最外层容器
  const screenRef = ref();
  // * 定时函数
  const timer = ref(0);
  // * 默认缩放值
  const scale = {
    width: "1",
    height: "1",
  };

  // * 设计稿尺寸（px）
  const baseWidth = width | 1920;
  const baseHeight = height | 1080;

  // * 需保持的比例（默认1.77778）
  const baseProportion = parseFloat((baseWidth / baseHeight).toFixed(5));
  const calcRate = () => {
    // 当前宽高比
    const currentRate = parseFloat(
      (window.innerWidth / window.innerHeight).toFixed(5)
    );
    if (screenRef.value) {
      if (currentRate > baseProportion) {
        // 表示更宽
        scale.width = (
          (window.innerHeight * baseProportion) /
          baseWidth
        ).toFixed(5);
        scale.height = (window.innerHeight / baseHeight).toFixed(5);
        screenRef.value.style.transform = `scale(${scale.width}, ${scale.height})`;
      } else {
        // 表示更高
        scale.height = (
          window.innerWidth /
          baseProportion /
          baseHeight
        ).toFixed(5);
        scale.width = (window.innerWidth / baseWidth).toFixed(5);
        screenRef.value.style.transform = `scale(${scale.width}, ${scale.height})`;
      }
    }
  };

  const resize = () => {
    clearTimeout(timer.value);
    timer.value = window.setTimeout(() => {
      calcRate();
    }, 200);
  };

  // 改变窗口大小重新绘制
  const windowDraw = () => {
    window.addEventListener("resize", resize);
  };

  // 改变窗口大小重新绘制
  const unWindowDraw = () => {
    window.removeEventListener("resize", resize);
  };

  return {
    screenRef,
    calcRate,
    windowDraw,
    unWindowDraw,
  };
}

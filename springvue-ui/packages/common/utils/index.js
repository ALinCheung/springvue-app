/**
 * 获取数组的下标
 * @param array
 * @param item
 * @param key
 * @returns {number}
 */
export const getIndex = (array, item, key) => {
  if (array && array.length > 0) {
    for (let i = 0; i < array.length; i++) {
      if (key && item && item[key] === array[i][key]) {
        return i;
      }
    }
  }
  return 0;
};

/**
 * 生成UUID
 * @returns {string}
 */
export const guid = () => {
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
    var r = (Math.random() * 16) | 0,
      v = c === "x" ? r : (r & 0x3) | 0x8;
    return v.toString(16);
  });
};

/**
 * 构建Promise队列
 * @param arr
 * @returns {Promise<void>}
 */
export const promiseQueue = (arr) => {
  var sequence = Promise.resolve();
  arr.forEach((item) => {
    sequence = sequence.then(item);
  });
  return sequence;
};

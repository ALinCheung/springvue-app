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

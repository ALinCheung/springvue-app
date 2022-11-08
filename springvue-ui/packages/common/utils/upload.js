import { guid } from "./index.js";
import request from "./request.js";

//正常上传
const upload = (url, data, progress) => {
  return new Promise((resolve, reject) => {
    request({
      url,
      method: "post",
      data,
      headers: {
        "Content-Type": "multipart/form-data",
      },
      onUploadProgress: (progressEvent) => {
        if (progress) {
          progress(
            ((progressEvent.loaded / progressEvent.total) * 100).toFixed(0) | 0
          );
        }
      },
    })
      .then((res) => {
        return resolve(res);
      })
      .catch((err) => {
        return reject(err);
      });
  });
};

//分片上传
const uploadByPieces = async (
  url,
  data,
  file,
  progress = (loaded) => {},
  size = 5
) => {
  // 上传过程中用到的变量
  const uuid = guid(); // 5MB一片
  const chunkSize = size * 1024 * 1024; // 5MB一片
  const chunkCount = Math.ceil(file.size / chunkSize); // 总片数
  // 上传进度
  let progressTotal = [];
  progressTotal.length = chunkCount;
  // 获取当前chunk数据
  const getChunkInfo = (file, index) => {
    let start = index * chunkSize;
    let end = Math.min(file.size, start + chunkSize);
    let chunk = file.slice(start, end);
    return { start, end, chunk };
  };
  // 针对单个文件进行chunk上传
  const readChunk = (index, extraData) => {
    const { chunk } = getChunkInfo(file, index);
    let fetchForm = new FormData();
    fetchForm.append("uuid", uuid);
    fetchForm.append("fileName", file.name);
    fetchForm.append("chunk", chunk);
    fetchForm.append("index", index);
    fetchForm.append("chunkCount", chunkCount);
    // 额外参数
    if (extraData) {
      for (let key in extraData) {
        fetchForm.append(key, extraData[key]);
      }
    }
    // 进度条封装
    const progressWrapper = (loaded) => {
      let totalLoaded = 0;
      progressTotal[index] = loaded;
      progressTotal.forEach((value) => {
        totalLoaded += Math.ceil(value / chunkCount);
      });
      progress(totalLoaded >= 100 ? 100 : totalLoaded);
    };
    // 分片上传接口
    return upload(url, fetchForm, progressWrapper);
  };
  // 针对每个文件进行chunk处理
  const promiseList = [];
  try {
    for (let index = 0; index < chunkCount; ++index) {
      promiseList.push(readChunk(index, data));
    }
    return await Promise.all(promiseList);
  } catch (e) {
    return e;
  }
};

export { upload, uploadByPieces };

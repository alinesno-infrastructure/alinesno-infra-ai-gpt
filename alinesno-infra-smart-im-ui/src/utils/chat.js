/** 格式化消息内容 */
export function formatMessage(message, selectedUsers) {
  const words = message.split(' ');
  const formattedMessage = [];
  words.forEach((word) => {

  if (word.startsWith('@')) {
      const user = selectedUsers.find((u) => `@${u.roleName.toLowerCase()}` === word.toLowerCase());
      if (user) {
          formattedMessage.push({ type: 'mention', username: user.roleName , id: user.id });
      } else {
          formattedMessage.push({ type: 'text', text: word });
      }
    } else if (word.startsWith('#')) {
      const orderId = word.slice(1);
      formattedMessage.push({ type: 'business', businessId: orderId });
    } else {
      formattedMessage.push({ type: 'text', text: word });
    }

  });
  return formattedMessage;
}

/** 显示图片 */
export function imagePath(row){
  let roleAvatar = '1746435800232665090' ; 
  if(row.icon){
    roleAvatar = row.icon; 
  }
  return import.meta.env.VITE_APP_BASE_API + "/v1/api/infra/base/im/chat/displayImage/" + roleAvatar ; 
}
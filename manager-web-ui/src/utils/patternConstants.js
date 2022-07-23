const PATTERN_NORMAL_NAME = /^\w+$/g
const PATTERN_NORMAL_NAME_ZH = /^[\w\u4e00-\u9fa5]+$/g

const PATTERN_VERSION = /^\d+\.\d+\.\d+$/g
const PATTERN_DOMAIN = /^(?=^.{3,255}$)([a-zA-Z0-9]|[a-zA-Z0-9][-a-zA-Z0-9]{0,61}[a-zA-Z0-9])(\.([a-zA-Z0-9]|[a-zA-Z0-9][-a-zA-Z0-9]{0,61}[a-zA-Z0-9]))+$/g

export { PATTERN_NORMAL_NAME, PATTERN_NORMAL_NAME_ZH, PATTERN_VERSION, PATTERN_DOMAIN }

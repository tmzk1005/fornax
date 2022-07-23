const DefaultPaginationConf = {
  position: "bottom",
  showQuickJumper: true,
  showSizeChanger: true,
  pageSizeOptions: ["2", "10", "15", "20", "50", "100"],
  total: 0,
  pageSize: 15,
  current: 1,
  showTotal: (total) => "共" + total + "条",
}

export { DefaultPaginationConf }

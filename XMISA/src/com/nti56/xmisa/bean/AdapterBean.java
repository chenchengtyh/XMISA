package com.nti56.xmisa.bean;

import com.nti56.xmisa.util.StringUtils;

public class AdapterBean {// 所有属性在此转换。路径、有无、合格、等。
	
//	中国智造，慧及全球（）“”，。：；！@#￥%……&*——+{}【】、|‘’？/·~《》
//	ABCDEFGHIJKLMNOPQRSTUVWXYZ()"",.:;！@#￥%……&*_+{}[]\|'？/`·<>
//	abcdefghijklmnopqrstuvwxyz0123456789

	private String User;// 用户名
	private String Key;// 密码
	private String PPTaskID;// 父父主键
	private String PTaskID;// 父主键
	private String TaskID;// 主键
	private String ConveyID;// 调运单号
	private String Type;// 业务类型、途损类型
	private String PCNum;// 微机编号
	private String PlanNum;// 计划数量
	private String NCState;// 反馈状态
	private String CarNum;// 车牌号
	private String Provider;// 供应商/加工商
	private String GoodsID;// 货物编码
	private String GoodsName;// 货物名称
	private String Spec;// 规格
	private String FactNum;// 完成数量
	private String PlanWeight;// 计划重量
	private String FactWeight;// 完成重量
	private String LockState;// 锁定状态
	private String LossWeight;// 途损重量
	private String Rate;// 换算率
	private String BarCode;// 一维条码
	private String Mark;// 新增/删除 标记
	private String Date;// 日期
	private String Meridiem;// 上午/下午
	private String OrderID;// 单号/任务号
	private String Building;// 楼栋
	private String Person;// 巡查人员
	private String Floor;// 巡查楼层
	private String Point;// 巡查项目
	private String Result;// 巡查结果
	private String Trouble;// 隐患
	private String Picture;// 图片
	private String Solution;// 整改措施
	private String RepairDate;// 整改完成时间
	private String PictureVersion;// 图片版本
	private String CardNum;// 诱虫板编号
	private String SerricorneNum;// 烟草甲数量
	private String ElutellaNum;// 烟草粉螟数量
	private String Room;// 熏蒸室
	private String Method;// 熏蒸方法
	private String DosingDate;// 用药日期
	private String Progress;// 进度
	private String TrayID;// 货位编码
	private String TrayName;// 货位名称
	private String DateFirst;// 第一次检测日期
	private String CCTTFirst;// 第一次检测浓度
	private String DateSecond;// 第二次检测日期
	private String CCTTSecond;// 第二次检测浓度
	private String Drug;// 熏蒸药剂
	private String AirStart;// 通风开始日期
	private String AirEnd;// 通风结束日期
	private String Colligate;// 综合评定
	private String Appearance;// 外观
	private String Mould;// 霉变 无 轻微严重 0 1 2
	private String Mothy;// 虫蛀 无 轻微严重
	private String Odor;// 异味 无有 0 1
	private String Impurity;// 杂物 有无
	private String CheckLine;// 质检线
	private String TotalNum;// 质检总量
	private String AssignState;// 分配状态
	private String TrayNum;// 货位总量
	private String CheckNum;// 质检数量
	private String Weight;// 重量
	private String Water;// 水分
	private String RFIDCode;// RFID条码
	private String FactWater;// 实际水分
	private String WaterDev;// 水分偏差
	private String DensityDev;// 密度偏差
	private String Note;// 备注
	private String Color;// 颜色
	private String WaterFell;// 手感水分
	private String OilTrace;// 油印 无 轻微严重
	private String DetailNum;// 明细数量
	private String IvtState;// 盘点状态
	private String TrayTotle;// 盘点货位总数
	private String TrayDone;// 已盘点货位数
	private String IvtNum;// 盘点数量
	private String IvtDone;// 已完成数量
	private String IvtWait;// 待完成数量
	private String StockNum;// 库存数量
	private String StockWeight;// 库存重量
	private String IvtWeight;// 盘点重量
	private String CarID;// 抱车编号
	private String DoorID;// 门禁编号
	private String InTime;// 入库时间
	private String VbomName;// 生产牌号
	private String GoalWare;// 目标仓库
	private String OutTime;// 出库时间

	private String Volume;// 熏蒸体积
	private String Dosage;// 用药量
	private String Unit;// 用药单位
	private String Location;// 位置
	private String Concentration6;// 6小时浓度
	private String Concentration12;// 12小时浓度
	private String Concentration24;// 24小时浓度
	private String Concentration48;// 48小时浓度
	private String Concentration72;// 72小时浓度
	private String Concentration96;// 96小时浓度
	private String Concentration144;// 144小时浓度
	private String Concentration216;// 216小时浓度

	public String getNCState() {
		return NCState;
	}

	public void setNCState(String NcState) {
		if (NcState == null || NcState.equals("0")) {
			this.NCState = StringUtils.IsUpLoad.NO;
		} else if (NcState.equals("1")) {
			this.NCState = StringUtils.IsUpLoad.YES;
		} else {
			this.NCState = NcState;
		}
	}

	public String getColligate() {
		return Colligate;
	}

	public void setColligate(String colligate) {
		if (colligate == null || colligate.equals("0")) {
			this.Colligate = StringUtils.qualified;
		} else if (colligate.equals("1")) {
			this.Colligate = StringUtils.disqualified;
		} else {
			this.Colligate = colligate;
		}
	}

	public String getAppearance() {
		return Appearance;
	}

	public void setAppearance(String appearance) {
		if (appearance == null || appearance.equals("0")) {
			this.Appearance = StringUtils.qualified;
		} else if (appearance.equals("1")) {
			this.Appearance = StringUtils.disqualified;
		} else {
			this.Appearance = appearance;
		}
	}

	public String getColor() {
		return Color;
	}

	public void setColor(String color) {
		if (color == null || color.equals("0")) {
			this.Color = StringUtils.qualified;
		} else if (color.equals("1")) {
			this.Color = StringUtils.disqualified;
		} else {
			this.Color = color;
		}
	}

	public String getWaterFell() {
		return WaterFell;
	}

	public void setWaterFell(String waterFell) {
		if (waterFell == null || waterFell.equals("0")) {
			this.WaterFell = StringUtils.qualified;
		} else if (waterFell.equals("1")) {
			this.WaterFell = StringUtils.disqualified;
		} else {
			this.WaterFell = waterFell;
		}
	}

	public String getMould() {
		return Mould;
	}

	public void setMould(String mould) {
		if (mould == null || mould.equals("0")) {
			this.Mould = StringUtils.none;
		} else if (mould.equals("1")) {
			this.Mould = StringUtils.slight;
		} else if (mould.equals("2")) {
			this.Mould = StringUtils.severe;
		} else {
			this.Mould = mould;
		}
	}

	public String getOilTrace() {
		return OilTrace;
	}

	public void setOilTrace(String oilTrace) {
		if (oilTrace == null || oilTrace.equals("0")) {
			this.OilTrace = StringUtils.none;
		} else if (oilTrace.equals("1")) {
			this.OilTrace = StringUtils.slight;
		} else if (oilTrace.equals("2")) {
			this.OilTrace = StringUtils.severe;
		} else {
			this.OilTrace = oilTrace;
		}
	}

	public String getMothy() {
		return Mothy;
	}

	public void setMothy(String mothy) {
		if (mothy == null || mothy.equals("0")) {
			this.Mothy = StringUtils.none;
		} else if (mothy.equals("1")) {
			this.Mothy = StringUtils.slight;
		} else if (mothy.equals("2")) {
			this.Mothy = StringUtils.severe;
		} else {
			this.Mothy = mothy;
		}
	}

	public String getOdor() {
		return Odor;
	}

	public void setOdor(String odor) {
		if (odor == null || odor.equals("0")) {
			this.Odor = StringUtils.none;
		} else if (odor.equals("1")) {
			this.Odor = StringUtils.exist;
		} else {
			this.Odor = odor;
		}
	}

	public String getImpurity() {
		return Impurity;
	}

	public void setImpurity(String impurity) {
		if (impurity == null || impurity.equals("0")) {
			this.Impurity = StringUtils.none;
		} else if (impurity.equals("1")) {
			this.Impurity = StringUtils.exist;
		} else {
			this.Impurity = impurity;
		}
	}

	public String getMethod() {
		return Method;
	}

	public void setMethod(String method) {
		if (method == null) {
			this.Method = "";
		} else if (method.equals("YH001")) {
			this.Method = "熏蒸室熏蒸";
		} else if (method.equals("YH002")) {
			this.Method = "在库熏蒸杀虫(栋杀)";
		} else if (method.equals("YH007")) {
			this.Method = "在库熏蒸杀虫(层杀)";
		} else if (method.equals("YH008")) {
			this.Method = "在库熏蒸杀虫(垛杀)";
		} else {
			this.Method = method;
		}
	}

	public String getMeridiem() {
		return Meridiem;
	}

	public void setMeridiem(String meridiem) {
		if (meridiem == null) {
			this.Meridiem = "";
		} else if (meridiem.equals("0")) {
			this.Meridiem = "上午";
		} else if (meridiem.equals("1")) {
			this.Meridiem = "下午";
		} else {
			this.Meridiem = meridiem;
		}
	}

	// TODO
	public String getUser() {
		return User;
	}

	public void setUser(String user) {
		this.User = user;
	}

	public String getKey() {
		return Key;
	}

	public void setKey(String key) {
		this.Key = key;
	}

	public String getPPTaskID() {
		return PPTaskID;
	}

	public void setPPTaskID(String ppTaskId) {
		this.PPTaskID = ppTaskId;
	}

	public String getPTaskID() {
		return PTaskID;
	}

	public void setPTaskID(String pTaskId) {
		this.PTaskID = pTaskId;
	}

	public String getTaskID() {
		return TaskID;
	}

	public void setTaskID(String taskId) {
		this.TaskID = taskId;
	}

	public String getConveyID() {
		return ConveyID;
	}

	public void setConveyID(String conveyId) {
		this.ConveyID = conveyId;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		this.Type = type;
	}

	public String getPCNum() {
		return PCNum;
	}

	public void setPCNum(String PcNum) {
		this.PCNum = PcNum;
	}

	public String getPlanNum() {
		return PlanNum;
	}

	public void setPlanNum(String planNum) {
		this.PlanNum = planNum;
	}

	public String getCarNum() {
		return CarNum;
	}

	public void setCarNum(String carNum) {
		this.CarNum = carNum;
	}

	public String getGoodsID() {
		return GoodsID;
	}

	public void setGoodsID(String goodsID) {
		this.GoodsID = goodsID;
	}

	public String getGoodsName() {
		return GoodsName;
	}

	public void setGoodsName(String goodsName) {
		this.GoodsName = goodsName;
	}

	public String getSpec() {
		return Spec;
	}

	public void setSpec(String spec) {
		this.Spec = spec;
	}

	public String getFactNum() {
		return FactNum;
	}

	public void setFactNum(String factNum) {
		this.FactNum = factNum;
	}

	public String getPlanWeight() {
		return PlanWeight;
	}

	public void setPlanWeight(String planWeight) {
		this.PlanWeight = planWeight;
	}

	public String getFactWeight() {
		return FactWeight;
	}

	public void setFactWeight(String factWeight) {
		this.FactWeight = factWeight;
	}

	public String getLockState() {
		return LockState;
	}

	public void setLockState(String lockState) {
		this.LockState = lockState;
	}

	public String getLossWeight() {
		return LossWeight;
	}

	public void setLossWeight(String lossWeight) {
		this.LossWeight = lossWeight;
	}

	public String getRate() {
		return Rate;
	}

	public void setRate(String rate) {
		this.Rate = rate;
	}

	public String getBarCode() {
		return BarCode;
	}

	public void setBarCode(String barCode) {
		this.BarCode = barCode;
	}

	public String getMark() {
		return Mark;
	}

	public void setMark(String mark) {
		this.Mark = mark;
	}

	public String getProvider() {
		return Provider;
	}

	public void setProvider(String provider) {
		this.Provider = provider;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		this.Date = date;
	}

	public String getOrderID() {
		return OrderID;
	}

	public void setOrderID(String orderID) {
		this.OrderID = orderID;
	}

	public String getBuilding() {
		return Building;
	}

	public void setBuilding(String building) {
		this.Building = building;
	}

	public String getPerson() {
		return Person;
	}

	public void setPerson(String person) {
		this.Person = person;
	}

	public String getFloor() {
		return Floor;
	}

	public void setFloor(String floor) {
		this.Floor = floor;
	}

	public String getPoint() {
		return Point;
	}

	public void setPoint(String point) {
		this.Point = point;
	}

	public String getResult() {
		return Result;
	}

	public void setResult(String result) {
		this.Result = result;
	}

	public String getTrouble() {
		return Trouble;
	}

	public void setTrouble(String trouble) {
		this.Trouble = trouble;
	}

	public String getPicture() {
		return Picture;
	}

	public void setPicture(String picture) {
		this.Picture = picture;
	}

	public String getSolution() {
		return Solution;
	}

	public void setSolution(String solution) {
		this.Solution = solution;
	}

	public String getRepairDate() {
		return RepairDate;
	}

	public void setRepairDate(String repairDate) {
		this.RepairDate = repairDate;
	}

	public String getPictureVersion() {
		return PictureVersion;
	}

	public void setPictureVersion(String pictureVersion) {
		this.PictureVersion = pictureVersion;
	}

	public String getCardNum() {
		return CardNum;
	}

	public void setCardNum(String cardNum) {
		this.CardNum = cardNum;
	}

	public String getSerricorneNum() {
		return SerricorneNum;
	}

	public void setSerricorneNum(String serricorneNum) {
		this.SerricorneNum = serricorneNum;
	}

	public String getElutellaNum() {
		return ElutellaNum;
	}

	public void setElutellaNum(String elutellaNum) {
		this.ElutellaNum = elutellaNum;
	}

	public String getRoom() {
		return Room;
	}

	public void setRoom(String room) {
		this.Room = room;
	}

	public String getDosingDate() {
		return DosingDate;
	}

	public void setDosingDate(String dosingDate) {
		this.DosingDate = dosingDate;
	}

	public String getProgress() {
		return Progress;
	}

	public void setProgress(String progress) {
		this.Progress = progress;
	}

	public String getTrayID() {
		return TrayID;
	}

	public void setTrayID(String trayID) {
		this.TrayID = trayID;
	}

	public String getTrayName() {
		return TrayName;
	}

	public void setTrayName(String trayName) {
		this.TrayName = trayName;
	}

	public String getDateFirst() {
		return DateFirst;
	}

	public void setDateFirst(String dateFirst) {
		this.DateFirst = dateFirst;
	}

	public String getCCTTFirst() {
		return CCTTFirst;
	}

	public void setCCTTFirst(String ccttFirst) {
		this.CCTTFirst = ccttFirst;
	}

	public String getDateSecond() {
		return DateSecond;
	}

	public void setDateSecond(String dateSecond) {
		this.DateSecond = dateSecond;
	}

	public String getCCTTSecond() {
		return CCTTSecond;
	}

	public void setCCTTSecond(String ccttSecond) {
		this.CCTTSecond = ccttSecond;
	}

	public String getDrug() {
		return Drug;
	}

	public void setDrug(String drug) {
		this.Drug = drug;
	}

	public String getAirStart() {
		return AirStart;
	}

	public void setAirStart(String airStart) {
		this.AirStart = airStart;
	}

	public String getAirEnd() {
		return AirEnd;
	}

	public void setAirEnd(String airEnd) {
		this.AirEnd = airEnd;
	}

	public String getCheckLine() {
		return CheckLine;
	}

	public void setCheckLine(String checkLine) {
		this.CheckLine = checkLine;
	}

	public String getTotalNum() {
		return TotalNum;
	}

	public void setTotalNum(String totalNum) {
		this.TotalNum = totalNum;
	}

	public String getAssignState() {
		return AssignState;
	}

	public void setAssignState(String assignState) {
		this.AssignState = assignState;
	}

	public String getTrayNum() {
		return TrayNum;
	}

	public void setTrayNum(String trayNum) {
		this.TrayNum = trayNum;
	}

	public String getCheckNum() {
		return CheckNum;
	}

	public void setCheckNum(String checkNum) {
		this.CheckNum = checkNum;
	}

	public String getWeight() {
		return Weight;
	}

	public void setWeight(String weight) {
		this.Weight = weight;
	}

	public String getWater() {
		return Water;
	}

	public void setWater(String water) {
		this.Water = water;
	}

	public String getRFIDCode() {

		return RFIDCode;
	}

	public void setRFIDCode(String rfidCode) {
		this.RFIDCode = rfidCode;
	}

	public String getFactWater() {
		return FactWater;
	}

	public void setFactWater(String factWater) {
		this.FactWater = factWater;
	}

	public String getWaterDev() {
		return WaterDev;
	}

	public void setWaterDev(String waterDev) {
		this.WaterDev = waterDev;
	}

	public String getDensityDev() {
		return DensityDev;
	}

	public void setDensityDev(String densityDev) {
		this.DensityDev = densityDev;
	}

	public String getNote() {
		return Note;
	}

	public void setNote(String note) {
		this.Note = note;
	}

	public String getDetailNum() {
		return DetailNum;
	}

	public void setDetailNum(String detailNum) {
		this.DetailNum = detailNum;
	}

	public String getIvtState() {
		return IvtState;
	}

	public void setIvtState(String ivtState) {
		this.IvtState = ivtState;
	}

	public String getTrayTotle() {
		return TrayTotle;
	}

	public void setTrayTotle(String trayTotle) {
		this.TrayTotle = trayTotle;
	}

	public String getTrayDone() {
		return TrayDone;
	}

	public void setTrayDone(String trayDone) {
		this.TrayDone = trayDone;
	}

	public String getIvtNum() {
		return IvtNum;
	}

	public void setIvtNum(String ivtNum) {
		this.IvtNum = ivtNum;
	}

	public String getIvtDone() {
		return IvtDone;
	}

	public void setIvtDone(String ivtDone) {
		this.IvtDone = ivtDone;
	}

	public String getIvtWait() {
		return IvtWait;
	}

	public void setIvtWait(String ivtWait) {
		this.IvtWait = ivtWait;
	}

	public String getStockNum() {
		return StockNum;
	}

	public void setStockNum(String stockNum) {
		this.StockNum = stockNum;
	}

	public String getStockWeight() {
		return StockWeight;
	}

	public void setStockWeight(String stockWeight) {
		this.StockWeight = stockWeight;
	}

	public String getIvtWeight() {
		return IvtWeight;
	}

	public void setIvtWeight(String ivtWeight) {
		this.IvtWeight = ivtWeight;
	}

	public String getCarID() {
		return CarID;
	}

	public void setCarID(String carID) {
		this.CarID = carID;
	}

	public String getDoorID() {
		return DoorID;
	}

	public void setDoorID(String doorID) {
		this.DoorID = doorID;
	}

	public String getInTime() {
		return InTime;
	}

	public void setInTime(String inTime) {
		this.InTime = inTime;
	}

	public String getOutTime() {
		return OutTime;
	}

	public void setOutTime(String outTime) {
		this.OutTime = outTime;
	}

	public void setVbomName(String vbomName) {
		this.VbomName = vbomName;
	}

	public String getVbomName() {
		return VbomName;
	}

	public String getGoalWare() {
		return GoalWare;
	}

	public void setGoalWare(String goalWare) {
		this.GoalWare = goalWare;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		this.Unit = unit;
	}

	public String getVolume() {
		return Volume;
	}

	public void setVolume(String volume) {
		this.Volume = volume;
	}

	public String getDosage() {
		return Dosage;
	}

	public void setDosage(String dosage) {
		this.Dosage = dosage;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String location) {
		this.Location = location;
	}

	public String getConcentration6() {
		return Concentration6;
	}

	public void setConcentration6(String concentration6) {
		this.Concentration6 = concentration6;
	}

	public String getConcentration12() {
		return Concentration12;
	}

	public void setConcentration12(String concentration12) {
		this.Concentration12 = concentration12;
	}

	public String getConcentration24() {
		return Concentration24;
	}

	public void setConcentration24(String concentration24) {
		this.Concentration24 = concentration24;
	}

	public String getConcentration48() {
		return Concentration48;
	}

	public void setConcentration48(String concentration48) {
		this.Concentration48 = concentration48;
	}

	public String getConcentration72() {
		return Concentration72;
	}

	public void setConcentration72(String concentration72) {
		this.Concentration72 = concentration72;
	}

	public String getConcentration96() {
		return Concentration96;
	}

	public void setConcentration96(String concentration96) {
		this.Concentration96 = concentration96;
	}

	public String getConcentration144() {
		return Concentration144;
	}

	public void setConcentration144(String concentration144) {
		this.Concentration144 = concentration144;
	}

	public String getConcentration216() {
		return Concentration216;
	}

	public void setConcentration216(String concentration216) {
		this.Concentration216 = concentration216;
	}

}

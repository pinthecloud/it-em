package com.pinthecloud.item.model;



public class LikeIt extends AbstractItemModel<LikeIt>{
	
	public LikeIt() {
		
	}
	public LikeIt(String whoMade, String whoMadeId, String refId) {
		this.setWhoMade(whoMadeId);
		this.setWhoMadeId(whoMadeId);
		this.setRefId(refId);
	}
	
	@Override
	public final String getContent() {
		return null;
	}
	@Override
	public final void setContent(String content) {
	}
	
}

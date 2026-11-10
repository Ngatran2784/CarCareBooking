package com.carcare.model;

import java.io.Serializable;

public class TechnicianSkill implements Serializable {
    private static final long serialVersionUID = 1L;

    private int skillId;
    private int staffId;
    private int serviceId;
    private String proficiencyLevel;

    public TechnicianSkill() {}

    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }

    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public String getProficiencyLevel() { return proficiencyLevel; }
    public void setProficiencyLevel(String proficiencyLevel) { this.proficiencyLevel = proficiencyLevel; }
}

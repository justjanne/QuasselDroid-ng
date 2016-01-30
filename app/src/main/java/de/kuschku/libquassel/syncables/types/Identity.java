package de.kuschku.libquassel.syncables.types;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;
import java.util.Map;

import de.kuschku.libquassel.BusProvider;
import de.kuschku.libquassel.Client;
import de.kuschku.libquassel.functions.types.InitDataFunction;
import de.kuschku.libquassel.primitives.types.QVariant;
import de.kuschku.libquassel.syncables.Syncable;
import de.kuschku.libquassel.syncables.serializers.BufferSyncerSerializer;
import de.kuschku.libquassel.syncables.serializers.IdentitySerializer;

public class Identity extends SyncableObject<Identity> {
    @Syncable
    private String identityName;
    @Syncable
    private List<String> nicks;
    @Syncable
    private String ident;
    @Syncable
    private String realName;
    @Syncable(userType = "IdentityId")
    private int identityId;

    @Syncable
    private boolean autoAwayEnabled;
    @Syncable
    private boolean autoAwayReasonEnabled;
    @Syncable
    private int autoAwayTime;
    @Syncable
    private boolean awayNickEnabled;
    @Syncable
    private boolean awayReasonEnabled;
    @Syncable
    private boolean detachAwayEnabled;
    @Syncable
    private boolean detachAwayReasonEnabled;

    @Syncable
    private String awayReason;
    @Syncable
    private String autoAwayReason;
    @Syncable
    private String detachAwayReason;

    @Syncable
    private String partReason;
    @Syncable
    private String quitReason;
    @Syncable
    private String awayNick;

    @Syncable
    private String kickReason;

    public Identity(String identityName, List<String> nicks, String ident, String realName, int identityId, boolean autoAwayEnabled, boolean autoAwayReasonEnabled, int autoAwayTime, boolean awayNickEnabled, boolean awayReasonEnabled, boolean detachAwayEnabled, boolean detachAwayReasonEnabled, String awayReason, String autoAwayReason, String detachAwayReason, String partReason, String quitReason, String awayNick, String kickReason) {
        this.identityName = identityName;
        this.nicks = nicks;
        this.ident = ident;
        this.realName = realName;
        this.identityId = identityId;
        this.autoAwayEnabled = autoAwayEnabled;
        this.autoAwayReasonEnabled = autoAwayReasonEnabled;
        this.autoAwayTime = autoAwayTime;
        this.awayNickEnabled = awayNickEnabled;
        this.awayReasonEnabled = awayReasonEnabled;
        this.detachAwayEnabled = detachAwayEnabled;
        this.detachAwayReasonEnabled = detachAwayReasonEnabled;
        this.awayReason = awayReason;
        this.autoAwayReason = autoAwayReason;
        this.detachAwayReason = detachAwayReason;
        this.partReason = partReason;
        this.quitReason = quitReason;
        this.awayNick = awayNick;
        this.kickReason = kickReason;
    }

    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    public List<String> getNicks() {
        return nicks;
    }

    public void setNicks(List<String> nicks) {
        this.nicks = nicks;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getIdentityId() {
        return identityId;
    }

    public void setIdentityId(int identityId) {
        this.identityId = identityId;
    }

    public boolean isAutoAwayEnabled() {
        return autoAwayEnabled;
    }

    public void setAutoAwayEnabled(boolean autoAwayEnabled) {
        this.autoAwayEnabled = autoAwayEnabled;
    }

    public boolean isAutoAwayReasonEnabled() {
        return autoAwayReasonEnabled;
    }

    public void setAutoAwayReasonEnabled(boolean autoAwayReasonEnabled) {
        this.autoAwayReasonEnabled = autoAwayReasonEnabled;
    }

    public int getAutoAwayTime() {
        return autoAwayTime;
    }

    public void setAutoAwayTime(int autoAwayTime) {
        this.autoAwayTime = autoAwayTime;
    }

    public boolean isAwayNickEnabled() {
        return awayNickEnabled;
    }

    public void setAwayNickEnabled(boolean awayNickEnabled) {
        this.awayNickEnabled = awayNickEnabled;
    }

    public boolean isAwayReasonEnabled() {
        return awayReasonEnabled;
    }

    public void setAwayReasonEnabled(boolean awayReasonEnabled) {
        this.awayReasonEnabled = awayReasonEnabled;
    }

    public boolean isDetachAwayEnabled() {
        return detachAwayEnabled;
    }

    public void setDetachAwayEnabled(boolean detachAwayEnabled) {
        this.detachAwayEnabled = detachAwayEnabled;
    }

    public boolean isDetachAwayReasonEnabled() {
        return detachAwayReasonEnabled;
    }

    public void setDetachAwayReasonEnabled(boolean detachAwayReasonEnabled) {
        this.detachAwayReasonEnabled = detachAwayReasonEnabled;
    }

    public String getAwayReason() {
        return awayReason;
    }

    public void setAwayReason(String awayReason) {
        this.awayReason = awayReason;
    }

    public String getAutoAwayReason() {
        return autoAwayReason;
    }

    public void setAutoAwayReason(String autoAwayReason) {
        this.autoAwayReason = autoAwayReason;
    }

    public String getDetachAwayReason() {
        return detachAwayReason;
    }

    public void setDetachAwayReason(String detachAwayReason) {
        this.detachAwayReason = detachAwayReason;
    }

    public String getPartReason() {
        return partReason;
    }

    public void setPartReason(String partReason) {
        this.partReason = partReason;
    }

    public String getQuitReason() {
        return quitReason;
    }

    public void setQuitReason(String quitReason) {
        this.quitReason = quitReason;
    }

    public String getAwayNick() {
        return awayNick;
    }

    public void setAwayNick(String awayNick) {
        this.awayNick = awayNick;
    }

    public String getKickReason() {
        return kickReason;
    }

    public void setKickReason(String kickReason) {
        this.kickReason = kickReason;
    }

    @NonNull
    @Override
    public String toString() {
        return "Identity{" +
                "identityName='" + identityName + '\'' +
                ", nicks=" + nicks +
                ", ident='" + ident + '\'' +
                ", realName='" + realName + '\'' +
                ", identityId=" + identityId +
                ", autoAwayEnabled=" + autoAwayEnabled +
                ", autoAwayReasonEnabled=" + autoAwayReasonEnabled +
                ", autoAwayTime=" + autoAwayTime +
                ", awayNickEnabled=" + awayNickEnabled +
                ", awayReasonEnabled=" + awayReasonEnabled +
                ", detachAwayEnabled=" + detachAwayEnabled +
                ", detachAwayReasonEnabled=" + detachAwayReasonEnabled +
                ", awayReason='" + awayReason + '\'' +
                ", autoAwayReason='" + autoAwayReason + '\'' +
                ", detachAwayReason='" + detachAwayReason + '\'' +
                ", partReason='" + partReason + '\'' +
                ", quitReason='" + quitReason + '\'' +
                ", awayNick='" + awayNick + '\'' +
                ", kickReason='" + kickReason + '\'' +
                '}';
    }

    @Override
    public void init(@Nullable InitDataFunction function, @NonNull BusProvider provider, @NonNull Client client) {
        client.addIdentity(getIdentityId(), this);
    }

    @Override
    public void update(Identity from) {

    }

    @Override
    public void update(Map<String, QVariant> from) {
        update(IdentitySerializer.get().fromDatastream(from));
    }
}

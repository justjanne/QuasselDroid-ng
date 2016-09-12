/*
 * QuasselDroid - Quassel client for Android
 * Copyright (C) 2016 Janne Koschinski
 * Copyright (C) 2016 Ken Børge Viktil
 * Copyright (C) 2016 Magnus Fjell
 * Copyright (C) 2016 Martin Sandsmark <martin.sandsmark@kde.org>
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kuschku.quasseldroid_ng.ui.chat.dialogs;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.cryptacular.x509.dn.NameReader;
import org.cryptacular.x509.dn.StandardAttributeType;

import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.kuschku.libquassel.syncables.types.impl.CoreInfo;
import de.kuschku.quasseldroid_ng.R;
import de.kuschku.util.accounts.Account;
import de.kuschku.util.certificates.CertificateUtils;

public class CoreInfoDialogBuilder {
    private Context context;

    public CoreInfoDialogBuilder(Context context) {
        this.context = context;
    }

    public MaterialDialog build(Account account, CoreInfo coreInfo, X509Certificate[] certificateChain) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_coreinfo, null);
        CoreInfoViewHolder holder = new CoreInfoViewHolder(view);
        holder.bind(account, coreInfo, certificateChain);

        return new MaterialDialog.Builder(context)
                .customView(view, true)
                .backgroundColorAttr(R.attr.colorBackgroundDialog)
                .positiveColorAttr(R.attr.colorAccent)
                .positiveText(R.string.actionClose)
                .build();
    }

    private String issuerCN(X509Certificate certificate) {
        return new NameReader(certificate).readIssuer().getValue(StandardAttributeType.CommonName);
    }

    class CoreInfoViewHolder {
        @Bind(R.id.address)
        TextView address;

        @Bind(R.id.verified)
        TextView verified;

        @Bind(R.id.fingerprint)
        TextView fingerprint;

        @Bind(R.id.coreVersion)
        TextView coreVersion;

        @Bind(R.id.coreBuildDate)
        TextView coreBuildDate;

        @Bind(R.id.uptime)
        TextView uptime;

        @Bind(R.id.connected)
        TextView connected;

        public CoreInfoViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        public void bind(Account account, CoreInfo coreInfo, X509Certificate[] certificateChain) {
            address.setText(String.format(Locale.US, "%s:%d", account.host, account.port));
            if (certificateChain != null) {
                verified.setText(context.getString(R.string.labelCoreVerifier, issuerCN(certificateChain[0])));
                try {
                    fingerprint.setText(CertificateUtils.certificateToFingerprint(certificateChain[0]));
                } catch (NoSuchAlgorithmException | CertificateEncodingException e) {
                    fingerprint.setVisibility(View.GONE);
                }
            }
            coreVersion.setText(Html.fromHtml(coreInfo.quasselVersion()));
            coreBuildDate.setText(coreInfo.quasselBuildDate());
            uptime.setText(context.getString(R.string.labelCoreUptimeValue, DateUtils.getRelativeTimeSpanString(context, coreInfo.startTime().getMillis())));
            connected.setText(context.getString(R.string.labelCoreConnectedCientsValue, coreInfo.sessionConnectedClients()));
        }
    }
}

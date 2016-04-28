package no.ntnu.mikaelr.delta.view.signature;

import no.ntnu.mikaelr.delta.model.LogRecord;

import java.util.List;

public interface ActivityLogView {
    void updateView(List<LogRecord> logRecords);
}

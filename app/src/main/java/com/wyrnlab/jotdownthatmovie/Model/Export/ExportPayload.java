package com.wyrnlab.jotdownthatmovie.Model.Export;

import java.util.List;

public class ExportPayload {
    public int version;
    public String exportedAt;
    public List<ExportedItem> items;
}

//Tradução
package com.example.projetomarvel;

import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.common.model.DownloadConditions;

/** Device-side English to Brazilian Portuguese translation; failures preserve source text. */
public final class CharacterTranslator {
    public interface Callback { void onTranslated(String value); }
    private final Translator translator;
    public CharacterTranslator(){TranslatorOptions options=new TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH).setTargetLanguage(TranslateLanguage.PORTUGUESE).build();translator=Translation.getClient(options);}
    public void translate(String source,Callback callback){if(source==null||source.trim().isEmpty()){callback.onTranslated(source==null?"":source);return;}translator.downloadModelIfNeeded(new DownloadConditions.Builder().build()).addOnSuccessListener(v->translator.translate(source).addOnSuccessListener(callback::onTranslated).addOnFailureListener(e->callback.onTranslated(source))).addOnFailureListener(e->callback.onTranslated(source));}
    public void close(){translator.close();}
}

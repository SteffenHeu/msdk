/*
 * (C) Copyright 2015-2017 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1 as published by the Free
 * Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by the Eclipse Foundation.
 */

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.IO;
import de.unijena.bioinf.ChemistryBase.chem.PrecursorIonType;
import de.unijena.bioinf.ChemistryBase.ms.Ms2Experiment;
import de.unijena.bioinf.ChemistryBase.ms.Peak;
import de.unijena.bioinf.ChemistryBase.ms.Spectrum;
import de.unijena.bioinf.sirius.IdentificationResult;
import de.unijena.bioinf.sirius.IsotopePatternHandling;
import de.unijena.bioinf.sirius.Sirius;
import io.github.msdk.MSDKException;
import io.github.msdk.MSDKMethod;
import io.github.msdk.MSDKRuntimeException;
import io.github.msdk.datamodel.IonAnnotation;
import io.github.msdk.datamodel.MsSpectrum;
import io.github.msdk.datamodel.MsSpectrumType;
import io.github.msdk.datamodel.SimpleMsSpectrum;
import io.github.msdk.spectra.centroidprofiledetection.SpectrumTypeDetectionAlgorithm;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.annotation.Nullable;

/**
 * Created by evger on 14-May-18.
 */

public class SiriusIdentificationMethod implements MSDKMethod<List<IonAnnotation>> {

  private final Sirius sirius;
  private MsSpectrum ms1;
  private MsSpectrum ms2;
  private List<IonAnnotation> result;
  private double parentMass;
  private String adduct;
  private int numberOfCandidates;

  public SiriusIdentificationMethod(@Nullable MsSpectrum ms1, MsSpectrum ms2, double parentMass,
      String ion) {
    sirius = new Sirius();
    this.ms1 = ms1;
    this.ms2 = ms2;
    this.parentMass = parentMass;
    this.adduct = ion;
    numberOfCandidates = 5;
  }

  public void setNumberOfCandidates(int number) {
    numberOfCandidates = number;
  }

  public int getNumberOfCandidates() {
    return numberOfCandidates;
  }

  /**
   * This function is left here for custom spectrum files (just columns of mz and intensity values)
   * Does similar to mgf parser functionality
   */
  public static MsSpectrum readCustomMsFile(String path, String delimeter)
      throws IOException, MSDKRuntimeException {
    Scanner sc = new Scanner(new File(path));
    ArrayList<String> strings = new ArrayList<>();
    while (sc.hasNext()) {
      strings.add(sc.nextLine());
    }
    sc.close();

    int size = strings.size();
    double mz[] = new double[size];
    float intensity[] = new float[size];

    int index = 0;
    for (String s : strings) {
      String[] splitted = s.split(delimeter);
      if (splitted.length == 2) {
        mz[index] = Double.parseDouble(splitted[0]);
        intensity[index++] = Float.parseFloat(splitted[1]);
      } else {
        throw new MSDKRuntimeException("Incorrect spectrum structure");
      }
    }

    MsSpectrumType type = SpectrumTypeDetectionAlgorithm.detectSpectrumType(mz, intensity, size);
    return new SimpleMsSpectrum(mz, intensity, size, type);
  }


  /**
   * Transformation of MSDK data structures into Sirius structures
   */
  private List<IdentificationResult> siriusProcessSpectrums() throws MSDKException {
    Spectrum<Peak> siriusMs1 = null, siriusMs2;
    siriusMs2 = sirius
        .wrapSpectrum(ms2.getMzValues(), LocalArrayUtil.convertToDoubles(ms2.getIntensityValues()));
    if (ms1 != null) {
      siriusMs1 = sirius.wrapSpectrum(ms1.getMzValues(),
          LocalArrayUtil.convertToDoubles(ms1.getIntensityValues()));
    }

    PrecursorIonType precursor = sirius.getPrecursorIonType(adduct);
    Ms2Experiment experiment = sirius.getMs2Experiment(parentMass, precursor, siriusMs1, siriusMs2);

//    TODO: think about IsotopePatternHandling type
    List<IdentificationResult> siriusResults = sirius
        .identify(experiment, numberOfCandidates, true, IsotopePatternHandling.omit);
    return siriusResults;
  }

  @Nullable
  @Override
  public Float getFinishedPercentage() {
    return null;
  }

  @Nullable
  @Override
  public List<IonAnnotation> execute() throws MSDKException {
    result = new ArrayList<>();
    List<IdentificationResult> siriusSpectrums = siriusProcessSpectrums();

    return result;
  }

  @Nullable
  @Override
  public List<IonAnnotation> getResult() {
    return result;
  }

  // TODO: implement
  @Override
  public void cancel() {

  }
}
